
ranks = {}

# Load the reference list
for line in open('bnclists-lemma.num','r').readlines():
	parts = line.split(' ')
	word = parts[2].strip();
	rank = parts[1]
	ranks[word] = {}
	ranks[word]['ref'] = rank
	
# Load the specific list
for line in open('../kcl-content-apraisal/src/main/resources/wordlists/word-frequency.txt','r').readlines():
	parts = line.split(' ')
	word = parts[0]
	rank = parts[2].strip()
	if not ranks.has_key(word): 
		ranks[word] = {}
	ranks[word]['target'] = rank
	

ref_tot = 0
target_tot = 0
for word in ranks.keys():
	if( ranks[word].has_key('ref') and ranks[word].has_key('target')):
		ref_tot += float(ranks[word]['ref'])
		target_tot += float(ranks[word]['target'])
		
for word in ranks.keys():
	if( ranks[word].has_key('ref') and ranks[word].has_key('target')):
		ref_rank = float(ranks[word]['ref'])/ref_tot
		target_rank = float(ranks[word]['target'])/target_tot
		if ref_rank != 0:
			print word, ref_rank, target_rank, target_rank/ref_rank 
	elif(  ranks[word].has_key('target') ):
		target_rank = float(ranks[word]['target'])/target_tot
		print word, 0.0, target_rank
		