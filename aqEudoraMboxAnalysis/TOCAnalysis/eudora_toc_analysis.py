import sys
import binascii

#length (bytes) of structure components
header = 72		
footer = 32
msg_len = 218

#length (bytes) entry components
unknown = 50
timestamp = 32
sender = 64
subject = 64
remainder = 8
total = 218

f = open(sys.argv[1], 'rb')

fsummary = open(sys.argv[1].replace('.toc', '_toc_summary.txt'), 'wb')

#return the number of emails this toc indexes
def getMboxSize(toc):
	toc.seek(0,2)
	return (toc.tell() - header - footer) / 218

#return the name of the mbox this toc indexes
def getMboxName(toc):
	toc.seek(8)
	return toc.read(32)

attachment_count = 0

len = getMboxSize(f)
f.seek(header)
for x in range (len):
	mail = f.read(218)
	
	field_unknown = mail[0:49].replace('\x00', ' ')
	field_timestamp = mail[50:81].replace('\x00', ' ')
	field_from = mail[82:145].replace('\x00', ' ')
	field_subject = mail[146:209].replace('\x00', ' ')

	summary = str(x+1).center(5, ' ') + ' | ' + field_timestamp.rstrip().center(32, ' ') + ' | ' + field_from + ' | ' + field_subject
	fsummary.write(summary + '\n')
	
	if (ord(field_unknown[47]) & 0x80) == 0x80:
		attachment_count += 1

fsummary.write('\n')		#will just print a single line
fsummary.write('Mbox name: ' + getMboxName(f).replace('\x00', ' ') + '\n')
fsummary.write('Mbox size: ' + str(len) + '\n')
fsummary.write('Est. No. emails with attachments: ' + str(attachment_count) + '\n')

f.close()
fsummary.close()

