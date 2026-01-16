#!/usr/bin/env sh
# -----------------------------------------------------------------------------
# Maven Wrapper bootstrap script (minimal, POSIX sh)
# - Downloads maven-wrapper.jar if missing
# - Runs Maven via org.apache.maven.wrapper.MavenWrapperMain
# -----------------------------------------------------------------------------

set -eu

# Resolve base dir (project root)
BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
PROPS_FILE="$WRAPPER_DIR/maven-wrapper.properties"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"

# Read property helper
prop() {
  key="$1"
  if [ -f "$PROPS_FILE" ]; then
    # shellcheck disable=SC2002
    val=$(cat "$PROPS_FILE" | sed -n "s/^${key}=//p" | tail -n 1)
    printf "%s" "$val"
  fi
}

WRAPPER_URL=$(prop wrapperUrl)
if [ -z "${WRAPPER_URL}" ]; then
  WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
fi

# Download wrapper jar if missing
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "[mvnw] maven-wrapper.jar not found. Downloading..."
  mkdir -p "$WRAPPER_DIR"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$WRAPPER_URL" -o "$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$WRAPPER_URL" -O "$WRAPPER_JAR"
  else
    echo "[mvnw] ERROR: curl or wget is required to download Maven Wrapper." >&2
    exit 1
  fi
fi

# Pick Java
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=$(command -v java || true)
fi

if [ -z "${JAVA}" ]; then
  echo "[mvnw] ERROR: Java not found. Please install JDK 17+ and ensure java is on PATH." >&2
  exit 1
fi

# Execute Maven Wrapper
exec "$JAVA" ${JAVA_OPTS:-} -classpath "$WRAPPER_JAR" -Dmaven.multiModuleProjectDirectory="$BASE_DIR" org.apache.maven.wrapper.MavenWrapperMain "$@"
