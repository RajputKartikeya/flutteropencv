import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'processed_image_page.dart';
import 'package:flutter_image_compress/flutter_image_compress.dart';

class OpenCVPage extends StatefulWidget {
  final String imagePath;

  OpenCVPage({required this.imagePath});

  @override
  _OpenCVPageState createState() => _OpenCVPageState();
}

class _OpenCVPageState extends State<OpenCVPage> {
  static const platform = MethodChannel('com.example.omr/processor');

  Future<void> _sendImageForProcessing() async {
    try {
      // Compress the image using the flutter_image_compress plugin
      final Uint8List? compressedBytes = await FlutterImageCompress.compressWithFile(
        widget.imagePath,
        quality: 80, // You can adjust the compression quality
        format: CompressFormat.jpeg,
      );

      // Save the compressed image as a temporary file
      final tempDir = await getTemporaryDirectory();
      final file = File('${tempDir.path}/temp_compressed.jpg');
      await file.writeAsBytes(compressedBytes!);

      // Send the compressed image file path to the native code for further processing
      final List<int> processedImageBytes = await platform.invokeMethod('processImage', {'imagePath': file.path});

      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => ProcessedImagePage(processedImage: Uint8List.fromList(processedImageBytes)),
        ),
      );
    } catch (e) {
      print('Error sending image to native code: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('OpenCV Page'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.file(File(widget.imagePath)), // Display the selected image
            ElevatedButton(
              onPressed: _sendImageForProcessing,
              child: Text('Process'),
            ),
          ],
        ),
      ),
    );
  }
}
