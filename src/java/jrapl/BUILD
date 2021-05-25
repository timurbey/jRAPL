load("@rules_java//java:defs.bzl", "java_library")

JRAPL_API = glob([
  "*.java"
], exclude = ["EnergyCheckUtils.java", "PerfCheckUtils.java"])

java_library(
  name = "jrapl",
  visibility = ["//visibility:public"],
  srcs = JRAPL_API,
)

java_binary(
  name = "jrapl_check",
  main_class = "jrapl.Rapl",
  runtime_deps = [
    ":jrapl"
  ],
  data = [
    ":cpu-scaler-lib",
  ],
  jvm_flags = [
    "-Djava.library.path=src/java/jrapl"
  ]
)

genrule(
  name = "cpu-scaler-lib",
  srcs = ["//src/jrapl:libCPUScaler.so"],
  outs = ["libCPUScaler.so"],
  cmd = "cp -f $< $@",
)
