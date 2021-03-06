package uk.co.bsol.trezorj.core.trezors;

import com.google.common.base.Preconditions;
import com.google.protobuf.AbstractMessage;
import uk.co.bsol.trezorj.core.Trezor;

import java.io.*;
import java.net.Socket;

/**
 * <p>Trezor implementation to provide the following to applications:</p>
 * <ul>
 * <li>Access to a Trezor device over a socket</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SocketTrezor extends AbstractTrezor implements Trezor {

  private Socket socket = null;
  private DataOutputStream out = null;
  private DataInputStream in = null;

  private final String host;
  private final int port;

  /**
   * <p>Create a new socket connection to a Trezor device</p>
   *
   * @param host The host name or IP address (e.g. "192.168.0.1")
   * @param port The port (e.g. 3000)
   */
  public SocketTrezor(String host, int port) {

    Preconditions.checkNotNull(host, "'host' must be present");
    Preconditions.checkState(port > 0 && port < 65535, "'port' must be within range");

    this.host = host;
    this.port = port;

  }

  @Override
  public synchronized void connect() {

    Preconditions.checkState(socket == null, "Socket is already connected");

    try {

      // Attempt to open a socket to the host/port
      socket = new Socket(host, port);

      // Add buffered data streams for easy data manipulation
      out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 1024));
      in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), 1024));

      // Monitor the input stream
      monitorDataInputStream(in);

    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public synchronized void close() {

    Preconditions.checkNotNull(socket, "Socket is not connected");

    // Attempt to close the socket (also closes the in/out streams)
    try {
      socket.close();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public void sendMessage(AbstractMessage message) throws IOException {

    Preconditions.checkNotNull(message, "Message must be present");
    Preconditions.checkNotNull(out, "Socket has not been connected.");

    // Apply the message to
    writeMessage(message, out);

  }

}
