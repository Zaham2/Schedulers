
import java.util.ArrayList;
import java.util.Scanner;

public class SRJF {

	static int currTime;
	static int completedProcesses;
	static ArrayList<Process> finishedProcesses;

	public SRJF() {
		currTime = 0;
		finishedProcesses = new ArrayList<Process>();
		completedProcesses = 0;
	}

	public static ProcessList addToReadyQueue(ProcessList all, ProcessList ready) {

		while (all.size() != 0 && all.p.get(0).arrivalTime == currTime) {

			System.out.println("Process appended at t=" + currTime);
			Process temp = all.p.get(0);
			ready.p.add(temp);
			all.p.remove(0);

		}

		return ready;
	}

	class Process {

		int arrivalTime;
		int burstTime;
		int remainingTime;
		int waitingTime;
		int turnaroundTime;

		public Process(int a, int b) {
			arrivalTime = a;
			burstTime = b;

			remainingTime = b;
			waitingTime = 0;
			turnaroundTime = -1;
		}

		public Process(Process proc) {
			arrivalTime = proc.arrivalTime;
			burstTime = proc.burstTime;

			remainingTime = proc.remainingTime;
			waitingTime = proc.waitingTime;
			turnaroundTime = proc.turnaroundTime;
		}

		public void printProcess() {
			System.out.println();
			System.out.println("Arrival time:  " + arrivalTime);
			System.out.println("Burst time:  " + burstTime);
			System.out.println("Remaining Time:  " + remainingTime);
			System.out.println("Waiting time:  " + waitingTime);
			System.out.println("Turnaround time:  " + turnaroundTime);
			System.out.println();

		}

		public void execute() {

			if (this == null)
				return;

			if (this.remainingTime > 0) {
				this.remainingTime--;
				if (this.remainingTime == 0) {
					getTurnaround();
					getWaiting();
					finishedProcesses.add(this);
					System.out.println("Process finished");
				}
			}

			else
				System.out.println("\nExecution failed\nProcess already finished\n");

		}
		public Process getWaiting() {
			this.waitingTime = (this.turnaroundTime - this.burstTime);
			return this;
		}

		public Process getTurnaround() {
			this.turnaroundTime = currTime - this.arrivalTime;
			return this;
		}
	}

	class ProcessList {

		ArrayList<Process> p;

		public ProcessList(int n) {

			p = new ArrayList<Process>(n);
			Process pr;
			Scanner s = new Scanner(System.in);

			if (n > 0)
				System.out.println("Please enter Arrival time and Burst time for each process");

			for (int i = 0; i < n; i++) {
				p.add(new Process(s.nextInt(), s.nextInt()));
			}

			s.close();
		}

		public ArrayList<Process> sortArrival() // bubble sort
		{
			int n = p.size();
			for (int i = 0; i < n; i++) {
				for (int j = n - 1; j > 0; j--) {
					if (p.get(j).arrivalTime < p.get(j - 1).arrivalTime) {

						Process temp = p.get(j);
						p.set(j, p.get(j - 1));// = ps.get(j - 1);
						p.set(j - 1, temp); // = temp;
					}

				}
			}
			return p;

		}

		public void printList() {
			for (int i = 0; i < p.size(); i++) {
				System.out.println(p.get(i).arrivalTime + " " + p.get(i).burstTime);
			}
		}

		public int size() {
			return p.size();
		}

		// Below are SRJF Specific processes

		public void execute(Process pr) {

			currTime++;

			if (pr == null) {
				System.out.println("This process is empty");
				return;

			} else if (pr.remainingTime == 0) {
				// System.out.println("You shouldn't send me an empty process :)");
				return;
				// addProcessToFinishedQueue(pr);
				// this.p.remove(pr);

			} else if (pr.remainingTime > 0) {
				pr.remainingTime = pr.remainingTime - 1;
				System.out.println("Executing process...\n" + "RT after execution:  " + pr.remainingTime);
				if (pr.remainingTime == 0) {

					System.out.println("\nProcess removed\n");
					//pr = this.getTurnaround(pr);
					//pr = this.getWaiting(pr);
					completedProcesses++;
					this.p.remove(pr);
					this.printList();
				}

			}

		}

		public void addProcessToFinishedQueue(Process pr) {

			System.out.println("Adding to finished queue");
			finishedProcesses.add(new Process(pr));

		}


		public Process getLowestRT() {

			if (p.size() == 0) {

				System.out.println("The queue is empty\nReturning null");
				return null;

			} else if (p.size() == 1) {

				return p.get(0);

			} else {

				Process lowest = p.get(0);

				for (Process proc : p) {
					if (proc.remainingTime < lowest.remainingTime)
						lowest = proc;
				}

				return lowest;
			}
		}

	}

	public static void main(String[] args) {
		SRJF h = new SRJF();
		Scanner scan = new Scanner(System.in);

		System.out.println("Number of processes");
		int n = scan.nextInt();

		ProcessList allProcesses = h.new ProcessList(n);
		System.out.println("Sorted input:\n");
		allProcesses.sortArrival();
		allProcesses.printList();

		ProcessList rq = h.new ProcessList(0);

		/*
		 * while (completedProcesses < n) {
		 * 
		 * rq = h.addToReadyQueue(allProcesses, rq); rq.execute(rq.getLowestRT()); //
		 * System.out.println("completedProcesses so far: " + completedProcesses); }
		 */

		beginExecute(allProcesses, rq);

		for (Process pr : finishedProcesses)
			pr.printProcess();
	}

	public static void beginExecute(ProcessList all, ProcessList rq) {

		// first add elements to the ready queue
		while (all.size() > 0 || rq.size() > 0) {
			addToReadyQueue(all, rq);

			// now get the lowest element in this queue
			Process lowest = rq.getLowestRT();
			
			if(lowest == null)
			{
				currTime++;
				continue;
			}
			
			else{
				currTime++;
				lowest.execute();

			if (lowest.remainingTime == 0) // so I deleted this object
				System.out.println("Removing process from readyQueue " + rq.p.remove(lowest) + "\n");

			// Now I want to add the next elements to the queue
			// then get the lowest
			// then execute THAT
			}
		}
	}

}