import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:flutter/services.dart';

class ResultPage extends StatefulWidget {
  @override
  _ResultPageState createState() => _ResultPageState();
}

class _ResultPageState extends State<ResultPage> {
  String? resultString;

  // Use the same MethodChannel name that you defined in your Java code
  final platform = const MethodChannel('com.example.omr/processor');

  @override
  void initState() {
    super.initState();
    _getResults();
  }

  _getResults() async {
    // Listen for the 'receiveResults' method call
    platform.setMethodCallHandler((MethodCall call) async {
      if (call.method == "receiveResults") {
        setState(() {
          resultString = call.arguments;
        });
      }
    });

    // Optionally, you can still invoke a method to initiate fetching the results
    try {
      final String result = await platform.invokeMethod('getResults');
      setState(() {
        resultString = result;
      });
    } on PlatformException catch (e) {
      print("Failed to get results: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    Map<String, dynamic>? resultsMap = json.decode(resultString ?? '{}');

    return Scaffold(
      appBar: AppBar(
        title: Text('Results'),
      ),
      body: Center(
        child: Text('Question 1: ${resultsMap?["question1"]}\nQuestion 2: ${resultsMap?["question2"]}'),
      ),
    );
  }
}
