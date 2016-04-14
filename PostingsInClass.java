for all terms {
	ti;
	for all docs {
		read doc;
		tokenizer;
		string compare;
		if (doc j contains ti) {
			postings.add(docj);
		}
		if (i == 0) {
			if (docj contains ti) {
				TID[i][idx] = ti;
			}
		}
	}
}

if (i > 0) {
	for (int n = 0; n< TID[j/*j is index for documents*/leng; n++]) {
		Utility.search (TID[j], i);
		if (found) {
			postings.add(i);
		}
	}
}

// We use the termInDocs[docID][termID] for the above