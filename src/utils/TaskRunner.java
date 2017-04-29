package utils;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class TaskRunner {
	static void run(Task task, String title, Runnable onSuccess) {
		Stage stage = new Stage();
		stage.setTitle("Please wait...");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.UTILITY);

		ProgressBar bar = new ProgressBar();
		bar.progressProperty().bind(task.progressProperty());

		Label label = new Label(title);

		final VBox box = new VBox();
		box.setPadding(new Insets(30));
		box.setSpacing(5);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(label, bar);

		Scene scene = new Scene(box);
		stage.setScene(scene);

		task.setOnSucceeded(event -> {
			stage.close();
			onSuccess.run();
		});

		Thread thread = new Thread(task);
		thread.start();

		Task<Void> bufferTime = sleepTask(1000);
		Thread bufferThread = new Thread(bufferTime);
		bufferThread.start();
		bufferTime.setOnSucceeded(event -> {
			if (task.isRunning()) {
				stage.show();
			}
		});
	}

	static Task<Void> sleepTask(final int millis) {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}
}
