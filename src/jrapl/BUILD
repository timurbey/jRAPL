CPU_SCALER_CC = [
  "CPUScaler.c",
  "arch_spec.c",
  "dvfs.c",
  "msr.c",
]

CPU_SCALER_HDRS = [
  "CPUScaler.h",
  "arch_spec.h",
  "msr.h"
]

cc_library(
  name = "cpu-scaler-lib",
  srcs = CPU_SCALER_CC,
  hdrs = CPU_SCALER_HDRS,
  deps = [
    ":copy_jni_hdr_lib"
  ],
  alwayslink = True,
)

cc_binary(
    name = "libCPUScaler.so",
    visibility = ["//visibility:public"],
    linkshared = True,
    data = CPU_SCALER_HDRS,
    deps = [":cpu-scaler-lib"],
)

genrule(
  name = "copy_link_jni_md_header",
  srcs = ["@bazel_tools//tools/jdk:jni_md_header-linux"],
  outs = ["jni_md.h"],
  cmd = "cp -f $< $@",
)

genrule(
  name = "copy_link_jni_header",
  srcs = ["@bazel_tools//tools/jdk:jni_header"],
  outs = ["jni.h"],
  cmd = "cp -f $< $@",
)

cc_library(
  name = "copy_jni_hdr_lib",
  hdrs = [
      ":copy_link_jni_header",
      ":copy_link_jni_md_header",
  ],
  includes = ["."],
)
