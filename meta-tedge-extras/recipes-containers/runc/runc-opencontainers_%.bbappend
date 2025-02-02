# kirkstone only: Remove patch which is not required when using meta-lts-mixins/go
#
# The root cause is due to a runc-opencontainers patch a file that no longer exists
# (at least when using in the meta-lts-mixins/go kirkstone branch)
#
# Patch error:
# ------------
# ERROR: runc-opencontainers-1.1.4+gitAUTOINC+974efd2dfc-r0 do_patch: Applying patch '0001-Makefile-respect-GOBUILDFLAGS-for-runc-and-remove-re.patch' on target directory '/home/octocat/reuben/meta-tedge/kas/build/tmp/work/cortexa53-poky-linux/runc-opencontainers/1.1.4+gitAUTOINC+974efd2dfc-r0/git'
# CmdError('quilt --quiltrc /home/octocat/reuben/meta-tedge/kas/build/tmp/work/cortexa53-poky-linux/runc-opencontainers/1.1.4+gitAUTOINC+974efd2dfc-r0/recipe-sysroot-native/etc/quiltrc push', 0, "stdout: Applying patch 0001-Makefile-respect-GOBUILDFLAGS-for-runc-and-remove-re.patch
# can't find file to patch at input line 18
# Perhaps you used the wrong -p or --strip option?
# The text leading up to this was:
# --------------------------
# |From 0fe50d2ca4517f5e3070585040f35ace413acd44 Mon Sep 17 00:00:00 2001
# |From: Bruce Ashfield <bruce.ashfield@gmail.com>
# |Date: Tue, 24 Aug 2021 11:38:23 -0400
# |Subject: [PATCH] Makefile: respect GOBUILDFLAGS for runc and remove recvtty 
# | from static
# |
# |Signed-off-by: Chen Qi <Qi.Chen@windriver.com>
# |[bva: refreshed for release 1.0.2]
# |Signed-off-by: Bruce Ashfield <bruce.ashfield@gmail.com>
# |---
# | Makefile | 3 +--
# | 1 file changed, 1 insertion(+), 2 deletions(-)
# |
# |diff --git a/Makefile b/Makefile
# |index e3af9bc1..f9d6de96 100644
# |--- a/Makefile
# |+++ b/Makefile
# --------------------------
# No file to patch.  Skipping patch.
# 1 out of 1 hunk ignored
# Patch 0001-Makefile-respect-GOBUILDFLAGS-for-runc-and-remove-re.patch does not apply (enforce with -f)
#
# ------------
SRC_URI:remove = "file://0001-Makefile-respect-GOBUILDFLAGS-for-runc-and-remove-re.patch"
