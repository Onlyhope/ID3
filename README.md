# ID3 CSE 353 Homework 1

class Id3Node {
	Array nodeData;
	Array children;
	
	double Entropy;
	int posCount;
	int negCount;
	String classifyingAttribute;
}

# Recursive method that will spilt node until no more classifiers or a pure set is found. #
bestSplit(node):
	
	# Finds node entropy, posCount, and negCount #
	IF (entropy == -1)
		determineEntropy(node)
	end IF

	IF (attributes.isEmpty())
		return
	end IF
	
	# Pure set is found #
	IF (entropy == 0):
		IF (posCount > negCount):
			answerNode.setAttribute("more")
		end IF
		ELSE IF (negCount > posCount):
			answerNode.setAttribute("less")
		end IF
		children.add(answerNode)	
	end IF
	
	bestGain = 1
	curGain = 0

	foreach (curAttribute : nodeData.attribute):
		# Returns children with calculated Entropy
		curChildren = split(curAttribute)
		# Calculate the weighted total entropy of children
		curGain = calcInfoGain(children)

		IF (pseudoInfoGain < bestGain)
			bestGain <- curGain
			bestAttribute <- curAttribute
			bestChildren <- curChildren
		end IF
	end LOOP

	foreach (children):
		bestSplit(child)
	end LOOP

	return
end FUNCTION

# Process node's data to calculate posCount and negCount. #
# Then use the above values to set the node's entropy value. #
determineEntropy(node):
	
	# Set posCount and negCount based on nodeData #
	processData(nodeData)
	
	total = posCount + negCount
	p = posCount / total
	n = negCount / total
	
	logP = log(p)/log(2)
	logN = log(n)/log(2)
	
	node.entropy = -(p * logP) - (n * logN)
	return
end FUNCTION

# Calculates the pseudo InfoGain, the summation of the classifier's weighted entropy. #
calcInfoGain(children):

	pseudoGain = 0	

	foreach (child : children):
		pseudoGain = pseudoGain + (child.entropy * childData.length)
	end LOOP
	
	pseudoGain /= allData.length

	return pseudoGain
end FUNCTION

# Calculates the splitInformation
calcSplitEntropy(children):

	splitEntropy = 0;
	subsetRatio = 0;
	log2Ratio = 0;
	setTotal = allData.length

	foreach (child : children):
		subsetRatio = (child.subsetSize(attribute) / setTotal)
		log2Ratio = log2(subsetRatio) / log2(2)
		splitEntropy = splitEntropy - (subsetRatio * log2Ratio)
	end LOOP

	return splitEntropy
end Function


