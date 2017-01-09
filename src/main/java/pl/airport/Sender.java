package pl.airport;

import java.io.IOException;

public interface Sender {
	 void sendToAll(String message) throws IOException;
}
