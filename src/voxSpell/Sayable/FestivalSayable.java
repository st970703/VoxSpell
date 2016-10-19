package voxSpell.Sayable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

/**
 * FestivalSayable is an abstract class used to say simple strings given using the sayWord() method.
 * @author mike
 *
 */
public abstract class FestivalSayable {

	@SuppressWarnings("rawtypes")
	private static SwingWorker previousWorker;

	/**
	 * Uses festival to pronounce the string passed in.
	 * @param word - a String containing what you want to pronounce
	 */
	public static void sayWord(final ArrayList<String> wordAL, final double pace, final String voice) {
		// if there is a SwingWorker still executing, ignore the request to say the words.
		if (previousWorker != null) {
			return;
		}
		// use SwingWorker to say the words if the inputs are valid
		if (wordAL != null & wordAL.size() > 0) {
			if (pace <= 2.2 && pace >= 0.8) {

				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						try {
							File file = new File(".tempSayText.scm");

							if (!file.exists()) {
								file.createNewFile();
							}

							BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile() ));

							bw.write("(" + "voice_" + voice + ")\n");

							bw.write("( Parameter.set  'Duration_Stretch  "+pace+" )");

							for (String s : wordAL) {
								bw.write("( SayText \"" + s +"??\" ) \n");
								bw.flush();
							}

							bw.close();

							ProcessBuilder pb = new ProcessBuilder("bash", "-c", "festival -b "+file.getName() );

							Process pro = pb.start();

							pro.waitFor();

						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						return null;
					}

					@Override
					public void done() {
						// remove the this from the SwingWorker record
						previousWorker = null;
					}

				};

				previousWorker = worker;
				worker.execute();

			}
		}
	}

}