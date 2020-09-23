package jrapl;

import java.lang.reflect.Field;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Rapl {
  private native static int ProfileInit();
  private native static int GetSocketNum();
  private native static void ProfileDealloc();

  public native static String EnergyStatCheck();

  // adapted from https://github.com/adamheinrich/native-utils
  private static void loadFromJar(String library) throws IOException {
    File temp = File.createTempFile(library, null);
    temp.deleteOnExit();

    if (!temp.exists()) {
      throw new FileNotFoundException("Could not create a temporary file.");
    }

    // Prepare buffer for data copying
    byte[] buffer = new byte[1024];
    int readBytes;

    try (InputStream is = Rapl.class.getResourceAsStream(library)) {
      if (is == null) {
        throw new FileNotFoundException("Could not find library " + library + " in jar.");
      }

      // Open output stream and copy data between source file in JAR and the temporary file
      try (OutputStream os = new FileOutputStream(temp)) {
        try {
          while ((readBytes = is.read(buffer)) != -1) {
            os.write(buffer, 0, readBytes);
          }
        } finally {
          System.load(temp.getAbsolutePath());
        }
      }
    }
  }

  public static int SOCKET_COUNT = 0;
  // value to add to energy diffs after underflow
	public static double WRAP_AROUND_ENERGY = 0;

  /**
   * Loads the CPUScaler library from within the jar.
   *
   * <p> This assumes that CPUScaler.so was packaged in the jar. If this is
   *     not the case, calls to
   */
	static {
		// try {
		// 	Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
		// 	fieldSysPath.setAccessible(true);
		// 	fieldSysPath.set(null, null);
		// } catch (Exception e) {
		// 	e.printStackTrace();
		// }

    // loadLibraryFromJar("/jrapl/libCPUScaler.so");
    try {
      loadFromJar("/jrapl/libCPUScaler.so");

      WRAP_AROUND_ENERGY = ProfileInit();
      SOCKET_COUNT = GetSocketNum();
    } catch (IOException e){
      // logger.warn();
    }
	}

	/**
   * @return an array of arrays of the current energy information by socket.
   *
   * <p> subarray structure is architecture dependent. Typically, 0 -> dram,
   *    1 -> cpu, 2 -> package.
   */
	public static double[][] getEnergyStats() {
    // guard if CPUScaler isn't available
    if (SOCKET_COUNT < 0) {
      return new double[0][0];
    }

		String EnergyInfo = EnergyStatCheck();
		if (SOCKET_COUNT == 1) { /*One Socket*/
			double[][] stats = new double[1][3];
			String[] energy = EnergyInfo.split("#");

			stats[0][0] = Double.parseDouble(energy[0]);
			stats[0][1] = Double.parseDouble(energy[1]);
			stats[0][2] = Double.parseDouble(energy[2]);

			return stats;
		} else { /*Multiple sockets*/
			String[] perSockEner = EnergyInfo.split("@");
			double[][] stats = new double[SOCKET_COUNT][3];
			int count = 0;

			for(int i = 0; i < perSockEner.length; i++) {
				String[] energy = perSockEner[i].split("#");
				for(int j = 0; j < energy.length; j++) {
					// count = i * 3 + j; //accumulative count
					stats[i][j] = Double.parseDouble(energy[j]);
				}
			}
			return stats;
		}
	}

  public static void DeallocProfile() {
		ProfileDealloc();
  }

  private Rapl() { }

  public static void main(String[] args) {
    System.out.println(getEnergyStats());
  }
}
