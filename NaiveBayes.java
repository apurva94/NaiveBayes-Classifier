import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class NaiveBayes {
	static String fileVocab;//console argument 1 stores vocabulary file
	static String fileMap;//console argument 2 stores file Map
	static String fileTrainLabel;//console argument 3 stores training label file
	static String fileTrainData;//console argument 4 stores training data file
	static String fileTestLabel;//console argument 5 stores testing label file
	static String fileTestData;//console argument 6 stores testing data file
	static double[][] pMLE=new double[20][61188];           
	static double[][] pBE=new double[20][61188];
	static double[] priorOfClasses= new double[20];
	static byte[][] documentWords=new byte[18824][61188];
	public static void main(String args[]) throws Exception{
		Scanner src=new Scanner (System.in);
		if ( args.length > 0 ) {
		    fileVocab = args[0];
		    fileMap = args[1];
		    fileTrainLabel = args[2];
		    fileTrainData = args[3];
		    fileTestLabel = args[4];
		    fileTestData = args[5];
		}
		
		String ch=" ";
		do{
			System.out.println("Enter choice: \n 1:Multinomial Model \n 2:Evaluation of Model \n 3:Performance on training\n 4:Performance on testing\n 5:exit");
			ch=src.next();
			switch(ch){
			case "1": 
				multinomial();
				break ;
			case "2": 
				System.out.println("Enter Document Id from 1 to 11269");
				int docId=src.nextInt();
				System.out.println(evaluation(docId));
				break;
			case "3":
				performance(fileTrainLabel);
				break ;
			case "4": 
				String ch1=" ";
				System.out.println("Enter choice: \n 1:With Baysian Estimate \n 2:With Max Likelihood estimate"); 
				ch1=src.next();
				switch(ch1){
				case "1": 
					performance(fileTestLabel);
					break ;
				case "2": 
					performanceMLE(fileTestLabel);
					break;
				default: 
					System.out.println("Invalid choice.");	
				}	
					
				break ;
			case "5": 
				System.out.println("Thank you for using this program");
				break;
			default: 
				System.out.println("Invalid choice. Try again");			
			}
		}while(!ch.equalsIgnoreCase("5"));
	}
	private static void performanceMLE(String file)throws NumberFormatException, IOException {
		/*
		 *Calculates the performance of model using MLE estimation
		 * We read the file containing labels and extract the class the document is supposed to be in
		 * if tour calculation match then we increment the values of accuracy and class accuracy
		 * finally we display performance parameters  
		*/
		double accuracy=0;
		int lineCount=0;
		double[] classAccuracy=new double[20];
		int confusion=0;
		int confusionMatrix[][]=new int[20][20];
		int[] classCount=new int[20];
		for(int i=0;i<20;i++){
			classAccuracy[i]=0;
			classCount[i]=0;
			for(int j=0;j<20;j++)
				confusionMatrix[i][j]=0;
		}
	
			String draw;
			BufferedReader Data = new BufferedReader( new FileReader ( file ));
			while ((draw = Data.readLine()) != null) {
				
				String[] pieces = draw.split(",");
				int drawnNumber = Integer.parseInt(pieces[0]); //class that it is supposed to be in
				classCount[drawnNumber-1]++;
				if(file.equalsIgnoreCase(fileTestLabel)){
					if(drawnNumber==(confusion=evaluationMLE((++lineCount)+11268))){
						accuracy++;
						classAccuracy[drawnNumber-1]++;
					}
				}
				else if(drawnNumber==(confusion=evaluationMLE(++lineCount))){
					accuracy++;
					classAccuracy[drawnNumber-1]++;
				}
				confusionMatrix[drawnNumber-1][confusion-1]++;			 	
			}
			accuracy/=lineCount;		
			for(int i=0;i<20;i++){
				classAccuracy[i]/=classCount[i];
				
			}
			
		
		System.out.println("Overall Accuracy="+accuracy+"\nClassAccuracy=");
		for(int i=0;i<20;i++)
			System.out.println("Class "+(i+1)+":"+classAccuracy[i]);
		System.out.println("\nConfusion Matrix:=\n\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10\t11\t12\t13\t14\t15\t16\t17\t18\t19\t20");
		for(int i=0;i<20;i++){
			System.out.print((i+1)+"\t");
			for(int j=0;j<20;j++)
				System.out.print(confusionMatrix[i][j]+" \t");
			System.out.println();
		}
		
	}
	private static int evaluationMLE(int docId) {
		/*
		 *Calculates the class of document using MLE estimation
		 * We use the formula w= argmaxw for pr(w)+summation of pr(xi|wj) for everyword xi
		 * here pr is MLE probabability
		*/
		int classDecision=-1;
		double argumentValue=0;
		double max=-999999999;
		for(int i=0;i<20;i++)
		{
			argumentValue+=(priorOfClasses[i]);
			double prodOfLog=1;
			for(int j=0;j<61188;j++){
				if(documentWords[docId-1][j]>0){
				//	for(int k=0;k<documentWords[docId-1][j];k++)
					prodOfLog*=(pMLE[i][j]);
				}
			}
			argumentValue*=prodOfLog;
			if(argumentValue>max){
				classDecision=i;
				max=argumentValue;
			}
		}
		
		
		return (classDecision+1);
	}
	private static void performance(String file) throws NumberFormatException, IOException {
		
		/*
		 *Calculates the performance of model using BE estimation
		 * We read the file containing labels and extract the class the document is supposed to be in
		 * if tour calculation match then we increment the values of accuracy and class accuracy
		 * finally we display performance parameters  
		*/
		double accuracy=0;
		int lineCount=0;
		double[] classAccuracy=new double[20];
		int confusion=0;
		int confusionMatrix[][]=new int[20][20];
		int[] classCount=new int[20];
		for(int i=0;i<20;i++){
			classAccuracy[i]=0;
			classCount[i]=0;
			for(int j=0;j<20;j++)
				confusionMatrix[i][j]=0;
		}
	
			String draw;
			BufferedReader Data = new BufferedReader( new FileReader ( file ));
			while ((draw = Data.readLine()) != null) {
				
				String[] pieces = draw.split(",");
				int drawnNumber = Integer.parseInt(pieces[0]);
				classCount[drawnNumber-1]++;
				if(file.equalsIgnoreCase(fileTestLabel)){
					if(drawnNumber==(confusion=evaluation((++lineCount)+11268))){
						accuracy++;
						classAccuracy[drawnNumber-1]++;
					}
				}
				else if(drawnNumber==(confusion=evaluation(++lineCount))){
					accuracy++;
					classAccuracy[drawnNumber-1]++;
				}
				confusionMatrix[drawnNumber-1][confusion-1]++;			 	
			}
			accuracy/=lineCount;		
			for(int i=0;i<20;i++){
				classAccuracy[i]/=classCount[i];
				
			}
			
		
		System.out.println("Overall Accuracy="+accuracy+"\nClassAccuracy=");
		for(int i=0;i<20;i++)
			System.out.println("Class "+(i+1)+":"+classAccuracy[i]);
		System.out.println("\nConfusion Matrix:=\n\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10\t11\t12\t13\t14\t15\t16\t17\t18\t19\t20");
		for(int i=0;i<20;i++){
			System.out.print((i+1)+"\t");
			for(int j=0;j<20;j++)
				System.out.print(confusionMatrix[i][j]+" \t");
			System.out.println();
		}
	}
	private static int evaluation(int docId) {
		/*
		 *Calculates the class of document using MLE estimation
		 * We use the formula w= argmaxw for pr(w)+summation of pr(xi|wj) for everyword xi
		 * here pr is BE probabability
		*/
		int classDecision=-1;
		double argumentValue=0;
		double max=-999999999;
		for(int i=0;i<20;i++)
		{
			argumentValue+=(priorOfClasses[i]);
			double prodOfLog=1;
			for(int j=0;j<61188;j++){
				if(documentWords[docId-1][j]>0){
					//for(int k=0;k<documentWords[docId-1][j];k++) 
					prodOfLog*=(pBE[i][j]);
				}
			}
			argumentValue*=prodOfLog;
			if(argumentValue>max){
				classDecision=i;
				max=argumentValue;
			}
		}
		
		
		return (classDecision+1);
	}
	
	private static void multinomial() {
		/*
		 Creates the model.
		 */
		
		try {
			BufferedReader TrainingLabel = new BufferedReader( new FileReader ( fileTrainLabel ));
			HashMap<Integer, ArrayList<Integer>> ListOfDocs=new HashMap<Integer,ArrayList<Integer>>();
			
			int[]totalWords=new int[20];
			int[][] wordCounts=new int[20][61188];
			int lineCount=0;
			String draw;
			
			for(int i=0;i<20;i++){
				priorOfClasses[i]=0;
				totalWords[i]=0;
				for(int j=0;j<61188;j++)
					wordCounts[i][j]=0;
			}
			for(int i=0; i<18824;i++)
				for(int j=0;j<61188;j++)
					documentWords[i][j]=0;
			//Calculating priors
			while ((draw = TrainingLabel.readLine()) != null) {
				
	            String[] pieces = draw.split(",");
			 	int drawnNumber = Integer.parseInt(pieces[0]);
				priorOfClasses[drawnNumber-1]++;
			 	if(ListOfDocs.get(drawnNumber-1)== null){
			 		ListOfDocs.put((drawnNumber-1),new ArrayList<Integer>());
			 	}
			 	ListOfDocs.get(drawnNumber-1).add(++lineCount);			 	
			}
			TrainingLabel.close();
			
			for(int i=0;i<priorOfClasses.length;i++)
				priorOfClasses[i]/=11269;
			for(int i=0;i<priorOfClasses.length;i++)			
				System.out.println("P(Omega = "+(i+1)+") ="+priorOfClasses[i]);
			
			
			//Calculating n and nk values
			String draw1;
			BufferedReader TrainingData = new BufferedReader( new FileReader ( fileTrainData ));
			while ((draw1 = TrainingData.readLine()) != null) {
				String[] pieces1 = draw1.split(",");
				int docNumber = Integer.parseInt(pieces1[0]);
				int wordID = Integer.parseInt(pieces1[1]);
				int wordCount = Integer.parseInt(pieces1[2]);
				
				int classNumber=-1;
				for(int i=0;i<20;i++)
					if(ListOfDocs.get(Integer.valueOf(i)).contains(docNumber))
						classNumber=i;
				wordCounts[classNumber][wordID-1]+=wordCount;
				totalWords[classNumber]+=wordCount;
				documentWords[docNumber-1][wordID-1]+=wordCount;
			}
			
			
			String draw2;
			BufferedReader TestingData = new BufferedReader( new FileReader ( fileTestData ));
			while ((draw2 = TestingData.readLine()) != null) {
				String[] pieces2 = draw2.split(",");
				int docNumber = Integer.parseInt(pieces2[0]);
				int wordID = Integer.parseInt(pieces2[1]);
				int wordCount = Integer.parseInt(pieces2[2]);
				documentWords[11269+docNumber-1][wordID-1]+=wordCount;
			}
			
			//Calculating MLE and BE
			for(int i=0;i<20;i++)
				for(int j=0;j<61188;j++){
					pMLE[i][j]=(double)wordCounts[i][j]/totalWords[i];
					pBE[i][j]=(double)(wordCounts[i][j]+1)/(totalWords[i]+61188);

				}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	
		
	}
}
