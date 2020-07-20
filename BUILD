load("@rules_java//java:defs.bzl", "java_binary", "java_library")

java_library(
  name = "jRAPL",
  srcs = glob(["src/java/jrapl/**/*.java"]),
  visibility = ["//visibility:public"],
)

java_binary(
    name = "run_energy_stats",
    srcs = ["src/java/jrapl/EnergyStats.java"],
    deps = [":jRAPL"],
    resources = ["//src/jrapl:libCPUScaler.so"],
    jvm_flags = ["-Djava.library.path=src/jrapl"],
    main_class = "jrapl.EnergyStats",
)
