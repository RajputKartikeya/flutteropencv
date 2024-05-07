  import 'dart:io';
  import 'dart:typed_data';
  import 'package:flutter/material.dart';
  import 'package:flutter/services.dart';
  import 'package:path_provider/path_provider.dart';
  import 'package:flutter_image_compress/flutter_image_compress.dart';
  import 'ResultPage.dart';

  class ProcessTemplatePage extends StatefulWidget {
    final String imagePath;

    ProcessTemplatePage({required this.imagePath});

    @override
    _ProcessTemplatePageState createState() => _ProcessTemplatePageState();
  }

  class _ProcessTemplatePageState extends State<ProcessTemplatePage> {
    static const platform = MethodChannel('com.example.omr/processor');
    Uint8List? processedImage;

    Future<void> _loadTemplate() async {
      try {
        final String result = await platform.invokeMethod('loadTemplate');
        print('Result: $result'); // Should print "Template loaded successfully."
      } catch (e) {
        print('Error loading template: $e');
      }
    }

    @override
    Widget build(BuildContext context) {
      return Scaffold(
        appBar: AppBar(
          title: Text('Process Image'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Expanded(
              child: Center(
                child: processedImage != null
                    ? Image.memory(processedImage!)
                    : Image.file(File(widget.imagePath)),
              ),
            ),
            ElevatedButton(
              onPressed: _loadTemplate,
              child: Text('Load Template'),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => ResultPage()),
                );
              },
              child: Text('Go to Result Page'),
            ),
          ],
        ),
      );
    }
  }
