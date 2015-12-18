package corpuscalc;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;

@SuppressWarnings("deprecation")
public class Calculator extends Configured implements Tool{

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{

		@SuppressWarnings("unused")
		private final static IntWritable one = new IntWritable(1);
		private Text pos = new Text();
		private Text keypair = new Text();
		
		@Override
		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> out, Reporter report)
				throws IOException {
			String line = value.toString().trim();
			String[] temp1;
			temp1=line.split("\t");
			String wordName = temp1[0].split(" ")[0];
			String position=temp1[0].split(" ")[1];
			String count=temp1[1];
			String valueOf=wordName + " " + count;
			String kp = position;
			pos.set(kp);
			keypair.set(valueOf);
			out.collect(pos, keypair);	
		}
	}
		
		public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, IntWritable> {
			public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
				Hashtable<String,Integer> ht=new Hashtable<String,Integer>();
				int sum = 0;
				while (values.hasNext()) {
					String temp=values.next().toString();
					ht.put(temp.split(" ")[0], Integer.parseInt(temp.split(" ")[1]));
					sum = sum + Integer.parseInt(temp.split(" ")[1]);
				}
				for (Entry<String, Integer> entry : ht.entrySet()) {
					IntWritable i=new IntWritable();
					Text t = new Text();
					String k = entry.getKey();
					String keytohash=k+ " " +key;
					t.set(keytohash);
					Integer v = entry.getValue();
					Float prob = (float) v/sum;
					Integer abc = (int) (prob*10000);
					i.set(abc);
					output.collect(t, i);
				}
			}
		
	}
	
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	
}