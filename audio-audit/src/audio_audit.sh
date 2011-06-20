#!/bin/bash

usage()
{
	echo -e "audio_audit: Audit a series of Wave files.\n"
	echo "OPTIONS:"
	echo "  -h    Show this message."
	echo "  -i    Input path (mandatory)."
	echo "  -f    Perform fingerprint verification of Master/Listening pairs."
	echo "  -v    Display version."
}

SOFTWARE_VERSION="audio_audit.sh/0.0.1"
INPUT=""
FINGERPRINT=""

while getopts "hfvi:" OPTION
do
	case $OPTION in
	h)
		usage
		exit 1
		;;
	f)
		FINGERPRINT=yes
		;;
	i)
		INPUT="$OPTARG"
		;;
	v)
		echo $SOFTWARE_VERSION
		exit
		;;
	esac
done

if [[ -z "$INPUT" ]]
then
	usage
	exit 1
fi

# Check that the BWFMetaEdit binary is on our PATH
if [[ ! -x $(which bwfmetaedit) ]]
then
	echo "Could not execute BWFMetaEdit binary." >&2
	exit 1
fi

#OS=unix
OS=windows
MASTER_RATE=96000
MASTER_BITS=24
LISTEN_RATE=44100
LISTEN_BITS=16

JHEARS_HOME="$(cygpath --$OS /cygdrive/c/AQuA/jhears-client-0.0.2/)"
JHEARS_CP="$(cygpath --$OS -p /cygdrive/c/AQuA/jhears-client-0.0.2/lib/*:)"
JHEARS_SERVER_HOME="$(cygpath --$OS /cygdrive/c/AQuA/jhears-miniserver-0.0.2/)"
JHEARS_SERVER_CP="$(cygpath --$OS -p /cygdrive/c/AQuA/jhears-miniserver-0.0.2/lib/*:)"
JHEARS_SERVER_BDB="$(cygpath --unix -p /cygdrive/c/AQuA/jhears-miniserver-0.0.2/var/data/bdb/)"
JHEARS_SERVER_HB="$(cygpath --unix -p /cygdrive/c/AQuA/jhears-miniserver-0.0.2/var/data/hibernate/)"

#Clear old fingerprints.
if [[ -n "$FINGERPRINT" ]]
then
	echo -n "Clearing JHears data..."
	rm "$JHEARS_SERVER_BDB"* 2> /dev/null
	rm "$JHEARS_SERVER_HB"* 2> /dev/null
	sleep 1
	echo "OK"
	#Start up the JHears server
	java -cp "$JHEARS_SERVER_CP" -Djhears.home="$JHEARS_SERVER_HOME" org.jhears.miniserver.Main &
	SERVER_PID="$!"
	echo "JHears server started ($SERVER_PID)."
fi

while read master
do
	master=$(cygpath --$OS "$master")
	LISTEN="${master%M.wav}L.wav"
	echo "Checking $master..."
	if [[ ! -e "$LISTEN" ]]
	then
		echo "$master: no corresponding Listening copy." >&2
	fi

#Check the Master copy.
	TECH="$(bwfmetaedit --out-tech "$master" | sed -n '2p')"
	RATE="$(echo "$TECH" | cut -d "," -f6)"
	[[ $RATE -eq $MASTER_RATE ]] || echo "Master $master does not have the correct sample rate (actual: $RATE)." >&2
	BITS="$(echo "$TECH" | cut -d "," -f8)"
	[[ $BITS -eq $MASTER_BITS ]] || echo "Master $master does not have the correct bit sample rate (actual: $BITS)." >&2

#Check the Listening copy.
	if [[ -e "$LISTEN" ]]
	then
		TECH="$(bwfmetaedit --out-tech "$LISTEN" | sed -n '2p')"
		RATE="$(echo "$TECH" | cut -d "," -f6)"	
		[[ $RATE -eq $LISTEN_RATE ]] || echo "Master $LISTEN does not have the correct sample rate (actual: $RATE)." >&2
		BITS="$(echo "$TECH" | cut -d "," -f8)"
		[[ $BITS -eq $LISTEN_BITS ]] || echo "Master $LISTEN does not have the correct bit sample rate (actual: $BITS)." >&2
	fi

#Generate and compare fingerprints.
	if [[ -n "$FINGERPRINT" ]]
	then
		echo -n "Generating fingerprint for $master..."
		MASTER_ID=$(java -cp "$JHEARS_CP" -Djhears.home="$JHEARS_HOME" org.jhears.JHearsClient "$master" -u "$(basename "${master%.wav}")")
		if [[ -z "$MASTER_ID" ]]
		then
			echo "ERROR"
			echo "Could not generate fingerprint for $master." >&2
		else
			echo "OK"
		fi
		
		if [[ -e "$LISTEN" ]] && [[ -n "$MASTER_ID" ]]
		then
			echo -n "Verifying fingerprint for $LISTEN..."
			LISTEN_ID="$(java -cp "$JHEARS_CP" -Djhears.home="$JHEARS_HOME" org.jhears.JHearsClient "$LISTEN" -s)"
			if [[ "$LISTEN_ID" == *$MASTER_ID* ]]
			then
				echo "OK ($(echo "$LISTEN_ID" | sed -rn 's@.*confidence: ([0-9]\.[0-9]+).+@\1@p'))"
			else
				echo "NONE"
			fi
		fi
	fi
done < <(find "$INPUT" -name "*M.wav")

[[ -n "$FINGERPRINT" ]] && kill $SERVER_PID