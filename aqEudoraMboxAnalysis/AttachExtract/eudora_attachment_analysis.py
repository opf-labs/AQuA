import sys
import os
from mailbox import mbox
import pprint

attachment_folder = '\\Attach\\'

mailbox = mbox(sys.argv[1])
length = mailbox.__len__()

asummary = open(sys.argv[1].replace('.mbx', '_attachment_summary.txt'), 'wb')

newdir1 = os.getcwd() + "\\emails_w_ext_attachments\\"

try:
	os.makedirs(newdir1)
except OSError as (errno, strerror):
    sys.stderr.write("OS error({0}): {1}".format(errno, strerror) + '\n')

attachment_count = 0
attach_exists = 0
attach_nonexist = 0

for x in range(length):

	fname1 = 'emails_w_ext_attachments\\' + str(x+1) + '.txt'
	
	mail = mailbox.get_message(x)

	payload = mail.get_payload()
	
	#output emails with attachments converted by Eudora
	if 'Attachment Converted:' in payload:
		if '>Attachment Converted:' not in payload:
			f1 = open(fname1, 'w')
			for stuff in mail.walk():
				f1.write(str(stuff))
			f1.close()
			
			f2 = open(fname1, 'r')
			for line in f2:
				if 'Attachment' in line:
					if '>Attachment' not in line:
						if '> Attachment' not in line:
						
							attachment_count += 1
							
							file_line = line.strip()
							
							file_name = file_line[(file_line.rfind('\\')+1):(file_line.rfind('"'))]
							nu_path = (os.getcwd() + attachment_folder + file_name)
							
							file_exists = os.path.exists(nu_path)
							
							if file_exists is False:
								attach_nonexist+=1
							else:
								attach_exists+=1
							
							summary = str(x).center(5, ' ') + ' | ' + str(attachment_count).center(5, ' ') + ' | ' + file_name.ljust(100, ' ') + ' | ' + str(file_exists).center(10, ' ') + ' | ' + file_line.replace('Attachment Converted: ', '')
							asummary.write(summary + '\n')
			
			f2.close()

asummary.write('\n')
asummary.write('Est. No. Attachments: ' + str(attachment_count) + '\n')
asummary.write('Attachments that can be found: ' + str(attach_nonexist) + '\n')
asummary.write('Attachments that cannot be found: ' + str(attach_exists) + '\n')

asummary.close()
