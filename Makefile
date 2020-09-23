CC = gcc
CFLAGS = -fPIC -g

SOURCE_DIR = src/jrapl
TEMP = $(SOURCE_DIR)/*.o
PERF_CHECK_TEMP = $(SOURCE_DIR)/perfCheck.o
CPU_SCALER_TEMP = $(SOURCE_DIR)/CPUScaler.o $(SOURCE_DIR)/arch_spec.o $(SOURCE_DIR)/dvfs.o $(SOURCE_DIR)/msr.o
TARGET = *.so

JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)include
JAVA_LINUX_INCLUDE = $(JAVA_INCLUDE)/linux
JNI_INCLUDE = -I$(JAVA_INCLUDE) -I$(JAVA_LINUX_INCLUDE)

JAVAC=javac

JAVA_SOURCES = src/java/jrapl/Rapl.java
JAVA_TEMP = jrapl
JAVA_TARGET = jrapl.jar

%.o: %.c
	$(CC) -c -o $@ $< $(CFLAGS) $(JNI_INCLUDE)

# libperfCheck.so: $(PERF_CHECK_TEMP)
# 	$(CC) $(JNI_INCLUDE) -shared -Wl,-soname,lib$@.so -o $@ $^ $(JNI_INCLUDE) -lc
#   rm -f $(PERF_CHECK_TEMP)

libCPUScaler.so: $(CPU_SCALER_TEMP)
	$(CC) -shared -Wl,-soname,$@ -o $@ $^ $(JNI_INCLUDE) -lc
	rm -f $(CPU_SCALER_TEMP)

# this doesn't work right
jrapl:
	mkdir $(JAVA_TEMP)
	javac $(JAVA_SOURCES) -d .

jar: libCPUScaler.so jrapl
	mv $(TARGET) $(JAVA_TEMP)
	jar --create --file $(JAVA_TARGET) $(JAVA_TEMP)
	rm -rf $(JAVA_TEMP) $(TARGET)

clean:
	rm -rf $(TARGET) $(TEMP) $(JAVA_TARGET) $(JAVA_TEMP)
