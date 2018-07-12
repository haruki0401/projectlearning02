package pro2;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
class NonHeaderObjectOutputStream extends ObjectOutputStream {
	protected void writeStreamHeader() {
	}

	NonHeaderObjectOutputStream(OutputStream out) throws IOException{
		super(out);
	}
}