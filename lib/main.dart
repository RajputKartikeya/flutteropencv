import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:image_picker/image_picker.dart';
import 'opencv_page.dart';
import 'ProcessTemplatePage.dart';  // Import the ProcessTemplatePage

void main() => runApp(const OMR());

class OMR extends StatelessWidget {
  const OMR({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'OMR Scanner',
      home: const ImagePickerApp(),
    );
  }
}

class ImagePickerApp extends StatefulWidget {
  const ImagePickerApp({Key? key}) : super(key: key);

  @override
  _ImagePickerAppState createState() => _ImagePickerAppState();
}

class _ImagePickerAppState extends State<ImagePickerApp> {
  File? _image;
  String? _selectedImagePath;
  static const platform = MethodChannel('com.example.omr/processor');

  Future<void> getImage(ImageSource source, String actionType) async {
    final pickedFile = await ImagePicker().pickImage(source: source);

    if (pickedFile != null && pickedFile.path != null) {
      setState(() {
        _selectedImagePath = pickedFile.path;
        _image = File(_selectedImagePath!);
      });

      try {
        await platform.invokeMethod('processImage', {'imagePath': _selectedImagePath});
      } catch (e) {
        print('Error sending image to native code: $e');
      }

      if (actionType == 'save') {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => OpenCVPage(imagePath: _selectedImagePath!),
          ),
        );
      } else if (actionType == 'process') {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ProcessTemplatePage(imagePath:_selectedImagePath!),  // Navigate to ProcessTemplatePage
          ),
        );
      }
    } else {
      print('No valid image path received.');
    }
  }

  Future<void> showImageSourceOptions(String actionType) async {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Choose Image Source'),
          actions: [
            ElevatedButton(
              child: const Text('Camera'),
              onPressed: () async {
                Navigator.pop(context);
                await getImage(ImageSource.camera, actionType);
              },
            ),
            ElevatedButton(
              child: const Text('Gallery'),
              onPressed: () async {
                Navigator.pop(context);
                await getImage(ImageSource.gallery, actionType);
              },
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('OMR Scanner'),
        centerTitle: true,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CustomButton(
              title: 'Upload Template',
              icon: Icons.upload_file,
              onClick: () => showImageSourceOptions('save'),
            ),
            CustomButton(
              title: 'Process Image',
              icon: Icons.play_arrow,
              onClick: () => showImageSourceOptions('process'),
            ),
          ],
        ),
      ),
    );
  }
}

Widget CustomButton({
  required String title,
  required IconData icon,
  required VoidCallback onClick,
}) {
  return Container(
    width: 200,
    child: ElevatedButton(
      onPressed: onClick,
      child: Row(
        children: [Icon(icon), SizedBox(width: 20), Text(title)],
      ),
    ),
  );
}
