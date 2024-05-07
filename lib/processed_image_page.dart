import 'dart:typed_data'; // Import for Uint8List
import 'ResultPage.dart';  // Add this import to use ResultPage
import 'package:flutter/material.dart';
import 'package:flutter/services.dart'; // Import for MethodChannel

class ProcessedImagePage extends StatefulWidget {
  final Uint8List processedImage; // Add this line

  ProcessedImagePage({required this.processedImage}); // Modify the constructor

  @override
  _ProcessedImagePageState createState() => _ProcessedImagePageState();
}

class _ProcessedImagePageState extends State<ProcessedImagePage> {

  static const platform = const MethodChannel('com.example.omr/processor');

  // Function to save template
  Future<void> _saveTemplate() async {
    try {
      await platform.invokeMethod('saveTemplate');
    } catch (e) {
      print("Failed to save template: '$e'.");
    }
  }

  // Function to load template
  Future<void> _loadTemplate() async {
    try {
      await platform.invokeMethod('loadTemplate');
    } catch (e) {
      print("Failed to load template: '$e'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Processed Image'),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Expanded(
            child: Center(
              child: Image.memory(widget.processedImage), // Use the passed image here
            ),
          ),
          ElevatedButton(
            onPressed: _saveTemplate,  // Method to save template
            child: Text('Save Template'),
          ),
        ],
      ),
    );
  }
}
