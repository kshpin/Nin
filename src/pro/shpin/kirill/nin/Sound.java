package pro.shpin.kirill.nin;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Sound {

	private Clip clip;

	public Sound(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
				DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
				// load the sound into memory
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(inputStream);
			} else throw new RuntimeException("Sound: File not found: " + fileName);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Sound: Malformed URL: " + e);
		} catch (UnsupportedAudioFileException e) {
			throw new RuntimeException("Sound: Unsupported audio file: " + e);
		} catch (IOException e) {
			throw new RuntimeException("Sound: Input/Output error: " + e);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("Sound: Line unavailable : " + e);
		}
	}

	public void play() {
		clip.setFramePosition(0);
		clip.start();
	}

	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void stop() {
		clip.stop();
	}
}
