package slimefinder.io;

import slimefinder.core.Mask;

import java.io.IOException;

public interface IDataLogger {
    void start() throws IOException;
    void close();
    void write(Mask m) throws IOException;
}