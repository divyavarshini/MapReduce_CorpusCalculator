# MapReduce_CorpusCalculator

The goal of the project is to find the top three sentence with the highest probability after the calculations. 
There are three sets of mappers and reducers used using the concept of chaining in order to achieve the same. 
The code can be used either on the Amazon's EMR or on Eclipse or the Linux test Script. 
The code to be executed on the EMR will require some sort of changes in the main project source files.
The main challenges of the project was making the code working on three different environments which was extensive. 
This gave a lot of experience with the Hadoop system on the local systems and as well as on the real cloud systems such as the Amazon services. 
The first mapper concentrates on counting the words and their positions together. 
The reducer here counted the total number of words in the same position. 
The second mapper took the results of the mapper one and finds out the number of sentences with at least N words and finally the third mapper includes the total probability calculation and finding the sentences.
