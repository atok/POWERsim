package agh.powerSim.gui.core.component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.control.TextArea;


/**
 * Specjalny pole typu textArea przechwytujace wyjście stdOut
 * zrobiłem bo chciałem sprawdzić czy się da. Może się przyda
 * @author Darek
 *
 */
public class OutputConsole extends TextArea {

	// private ExecutorService executor = Executors.newFixedThreadPool(1,new
	// ConsoleThreadFactory());

	// private class ConsoleThreadFactory implements ThreadFactory{
	//
	// @Override
	// public Thread newThread(Runnable r) {
	// Thread thread = new Thread(r);
	// thread.setDaemon(true);
	// return thread;
	// }
	//
	// }
	
	public OutputConsole() {
		setEditable(false);
		// executor.execute(new Runnable() {

		// @Override
		// public void run() {
		OutputStream stream = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				final int bb = b;
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						OutputConsole.this.appendText(String.valueOf((char) bb));

					}
				});
			}
		};

		PrintStream printStream = new PrintStream(stream);

		System.setOut(printStream);

		// while(true){
		// try {
		// TimeUnit.MILLISECONDS.wait(100);
		// } catch (InterruptedException e) {
		//
		// }
		// }
	}
	// });
	// }

}
