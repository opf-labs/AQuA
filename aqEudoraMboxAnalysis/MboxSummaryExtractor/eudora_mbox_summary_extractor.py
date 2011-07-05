import sys
import os
from mailbox import mbox
import pprint

mailbox = mbox(sys.argv[1])
len = mailbox.__len__()

fsummary = open(sys.argv[1].replace('.mbx', '_mbox_summary.txt'), 'wb')

newdir = os.getcwd() + "\\emails\\"

try:
	os.makedirs(newdir)
except OSError as (errno, strerror):
    sys.stderr.write("OS error({0}): {1}".format(errno, strerror) + '\n')

attachment_count = 0

for x in range(len):

	fname = 'emails\\' + str(x+1) + '.txt'
	mail = mailbox.get_message(x)

	if mail.__getitem__('From') is None:
		name = 'None'
	else:
		name = mail.__getitem__('From').replace('\x0D', ' ').replace('\x0A', ' ')
		
	if mail.__getitem__('Subject') is None:
		subject = 'None'
	else:
		subject = mail.__getitem__('Subject').replace('\x0D', ' ').replace('\x0A', ' ')	#remove newline characters found in 'Subject'

	if mail.__getitem__('Date') is None:
		date = 'None'
	else:
		date = mail.__getitem__('Date')

	summary = str(x+1).center(5, ' ') + ' | ' + date.center(50, ' ') + ' | ' + name.ljust(85, ' ') + ' | ' + subject.rstrip().ljust(85, ' ')
	fsummary.write(summary + '\n')
	
	#look for attachment based on the existence of Eudora 'Attachment Converted:' string
	payload = mail.get_payload()
	if 'Attachment Converted:' in payload:
		if '>Attachment Converted:' not in payload:	#discount emails in previous traces...
			attachment_count += 1
	
	#output emails and headers...
	f = open(fname, 'wb')
	for stuff in mail.walk():
		f.write(str(stuff))
	f.close()	

fsummary.write('\n')
fsummary.write('Mbox size: ' + str(len) + '\n')
#fsummary.write('Est. No. of attachments: ' + str(attachment_count) + '\n')

fsummary.close()


