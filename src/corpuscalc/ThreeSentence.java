package corpuscalc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;

@SuppressWarnings("deprecation")
public class ThreeSentence extends Configured implements Tool {


	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		Text t=new Text();
		Text constant=new Text();

		public void map(LongWritable key, Text value,  OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
		{
			constant.set("keyconstant");
			t.set(value.toString().trim());
			output.collect(constant,t);
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable> 
	{
		private URI[] localFiles;
		LinkedHashSet<String> sentencesStore = new  LinkedHashSet<String>();
		Hashtable<String,Double> ht=new Hashtable<String,Double>();
		public void configure(JobConf job)  {
			try {
				localFiles = DistributedCache.getCacheFiles(job);
				//File file=new File(localFiles[0].getName());
				@SuppressWarnings("resource")
				BufferedReader br= new BufferedReader(new FileReader("#Corpus.txt"));
				String s;
				while((s=br.readLine())!=null) {

					sentencesStore.add(s);
				}
			}	
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {

			String[] tempArray1={"","",""};
			Double[] tempArray2={0.0,0.0,0.0};

			while (values.hasNext()) {
				String temp=values.next().toString().trim();
				String[] tempArr = temp.split(" ");
				ht.put(tempArr[0].split("\t")[0]+" "+tempArr[0].split("\t")[1],Double.parseDouble(tempArr[1]));
			}
			Iterator<String> iter = sentencesStore.iterator();

			while(iter.hasNext()) {
				Double probsentence=1.0;
				int wc=1;
				String line=iter.next().toString().trim();
				//System.out.println(line);
				StringTokenizer tokenizer = new StringTokenizer(line);
		         while (tokenizer.hasMoreTokens()) {
		        	 Double prob=ht.get(Integer.toString(wc)+" "+ tokenizer.nextToken());
						probsentence*= ((double)prob/10000);
						wc=wc+1;
		         }

				if(probsentence>tempArray2[0]) {						
					tempArray2[2]=tempArray2[1];
					tempArray1[2]=tempArray1[1];

					tempArray2[1]=tempArray2[0];
					tempArray1[1]=tempArray1[0];

					tempArray2[0]=probsentence;			
					tempArray1[0]=line;
				}
				else if(probsentence>tempArray2[1]) {
					tempArray2[2]=tempArray2[1];
					tempArray1[2]=tempArray1[1];

					tempArray2[1]=probsentence;			
					tempArray1[1]=line;
				}
				else if(probsentence>tempArray2[2]){
					tempArray2[2]=probsentence;
					tempArray1[2]=line;
				}
			}
			Text sentence = new Text();
			DoubleWritable probability = new DoubleWritable();
			for(int i=0; i<3; i++) {
				sentence.set(tempArray1[i]);
				probability.set(tempArray2[i]);
				output.collect(sentence, probability);
			}

		}
	}

	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
}