package org.nvidyo.webapp.npv;

//Imports the Google Cloud client library
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;

/**
* Google Cloud TextToSpeech API sample application.
* Example usage: mvn package exec:java -Dexec.mainClass='com.example.texttospeech.SynthesizeText'
*                                      -Dexec.args='--text "hello"'
*/
public class TTS {

// [START tts_synthesize_text]
/**
* Demonstrates using the Text to Speech client to synthesize text or ssml.
*
* @param text the raw text to be synthesized. (e.g., "Hello there!")
* @throws Exception on TextToSpeechClient Errors.
*/
public static void synthesizeText(String text) throws Exception {
 // Instantiates a client
 try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
   // Set the text input to be synthesized
   SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

   // Build the voice request
   VoiceSelectionParams voice =
       VoiceSelectionParams.newBuilder()
           .setLanguageCode("en-US") // languageCode = "en_us"
           .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
           .build();

   // Select the type of audio file you want returned
   AudioConfig audioConfig =
       AudioConfig.newBuilder()
           .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
           .build();

   // Perform the text-to-speech request
   SynthesizeSpeechResponse response =
       textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

   // Get the audio contents from the response
   ByteString audioContents = response.getAudioContent();

   // Write the response to the output file.
   try (OutputStream out = new FileOutputStream("output.mp3")) {
     out.write(audioContents.toByteArray());
     System.out.println("Audio content written to file \"output.mp3\"");
   }
 }
}
// [END tts_synthesize_text]

// [START tts_synthesize_ssml]
/**
* Demonstrates using the Text to Speech client to synthesize text or ssml.
*
* <p>Note: ssml must be well-formed according to: (https://www.w3.org/TR/speech-synthesis/
* Example: <speak>Hello there.</speak>
*
* @param ssml the ssml document to be synthesized. (e.g., "<?xml...")
* @throws Exception on TextToSpeechClient Errors.
*/
public static void synthesizeSsml(String ssml) throws Exception {
 // Instantiates a client
 try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
   // Set the ssml input to be synthesized
   SynthesisInput input = SynthesisInput.newBuilder().setSsml(ssml).build();

   // Build the voice request
   VoiceSelectionParams voice =
       VoiceSelectionParams.newBuilder()
           .setLanguageCode("en-US") // languageCode = "en_us"
           .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
           .build();

   // Select the type of audio file you want returned
   AudioConfig audioConfig =
       AudioConfig.newBuilder()
           .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
           .build();

   // Perform the text-to-speech request
   SynthesizeSpeechResponse response =
       textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

   // Get the audio contents from the response
   ByteString audioContents = response.getAudioContent();

   // Write the response to the output file.
   try (OutputStream out = new FileOutputStream("output_ssml.mp3")) {
     out.write(audioContents.toByteArray());
     System.out.println("Audio content written to file \"output_ssml.mp3\"");
   }
 }
}
// [END tts_synthesize_ssml]
//[START tts_synthesize_ssml_file]
/**
* Demonstrates using the Text to Speech client to synthesize a text file or ssml file.
* @param ssmlFile the ssml document to be synthesized. (e.g., hello.ssml)
* @throws Exception on TextToSpeechClient Errors.
*/

public static void synthesizeSsmlFile(String ssmlFile)
   throws Exception {
 // Instantiates a client
 try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
   // Read the file's contents
   String contents = new String(Files.readAllBytes(Paths.get(ssmlFile)));
   // Set the ssml input to be synthesized
   SynthesisInput input = SynthesisInput.newBuilder()
       .setSsml(contents)
       .build();

   // Build the voice request
   VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
       .setLanguageCode("en-US") // languageCode = "en_us"
       .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
       .build();

   // Select the type of audio file you want returned
   AudioConfig audioConfig = AudioConfig.newBuilder()
       .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
       .build();

   // Perform the text-to-speech request
   SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
       audioConfig);

   // Get the audio contents from the response
   ByteString audioContents = response.getAudioContent();

   // Write the response to the output file.
   try (OutputStream out = new FileOutputStream("output.mp3")) {
     out.write(audioContents.toByteArray());
     System.out.println("Audio content written to file \"output.mp3\"");
   }
 }
}
// [END tts_synthesize_ssml_file]


/*public static void main(String... args) throws Exception {

	 ArgumentParser parser =
	     ArgumentParsers.newFor("SynthesizeText")
	         .build()
	         .defaultHelp(true)
	         .description("Synthesize a text or ssml.");
	
	 MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup().required(true);
	 group.addArgument("--text").help("The text file from which to synthesize speech.");
	 group.addArgument("--ssml").help("The ssml file from which to synthesize speech.");
	
	 try {
	   Namespace namespace = parser.parseArgs(args);
	
	   if (namespace.get("text") != null) {
	     synthesizeText(namespace.getString("text"));
	   } else {
	     synthesizeSsml(namespace.getString("ssml"));
		   //synthesizeSsmlFile(namespace.getString("ssml"));
	   }
	 } catch (ArgumentParserException e) {
		 
	   parser.handleError(e);
	 }
	}*/

}
