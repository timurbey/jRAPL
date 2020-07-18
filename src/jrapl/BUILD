package(default_visibility = ["//visibility:public"])

cc_library(
    name = "cpu-scaler-lib",
    srcs = ["CPUScaler.c", "arch_spec.c", "msr.c", "dvfs.c"],
    hdrs = ["CPUScaler.h", "arch_spec.h", "msr.h"],
    deps = ["//tools:copy_jni_hdr_lib"],
    alwayslink = True,
)

cc_binary(
    name = "libCPUScaler.so",
    linkshared = True,
    data = glob(["*.h"]),
    deps = [":cpu-scaler-lib"],
)
