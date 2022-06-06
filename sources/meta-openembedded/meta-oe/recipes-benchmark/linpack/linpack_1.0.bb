DESCRIPTION = "LINPACK Benchmarks are a measure of a system's floating point computing power"
SUMMARY = "LINPACK is a software library for performing numerical linear algebra on digital computers"

LICENSE = "PD"
LIC_FILES_CHKSUM ="file://${WORKDIR}/linpacknew.c;beginline=1;endline=23;md5=aa025e3bc44190c71e4c5e3b084fed87"

SRC_URI = "http://www.netlib.org/benchmark/linpackc.new;downloadfilename=linpacknew.c"
SRC_URI[md5sum] = "1c5d0b6a31264685d2e651c920e3cdf4"
SRC_URI[sha256sum] = "a63f2ec86512959f1fd926bfafb85905b2d7b7402942ffae3af374d48745e97e"

S = "${WORKDIR}"

do_compile () {
	${CC} ${CFLAGS} ${LDFLAGS} -o linpack linpacknew.c -lm
}

do_install () {
	install -Dm 0755 linpack ${D}${bindir}/linpack
}

