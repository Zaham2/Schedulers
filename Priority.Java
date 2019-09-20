import java.util.ArrayList;
import java.util.Scanner;

public class Priority {

	static int currTime;
	static ArrayList<Process> finishedProcesses;
	static int ageingTime = 5;

	public Priority() {
		currTime = 0;
		finishedProcesses = new ArrayList<Process>();
	}

	class Process {

		int arrivalTime;
		int burstTime;
		int waitingTime;
		int remainingTime;
		int turnaroundTime;
		int priority;

		public Process(int a, int b, int p) {
			arrivalTime = a;
			burstTime = b;
			priority = p;

			remainingTime = b;
			waitingTime = -1;
			turnaroundTime = -1;
		}

		public Process(Process proc) {
			arrivalTime = proc.arrivalTime;
			burstTime = proc.burstTime;

			remainingTime = proc.burstTime;
			priority = proc.priority;
			waitingTime = proc.waitingTime;
			turnaroundTime = proc.turnaroundTime;
		}

		public void printProcess() {
			System.out.println();
			System.out.println("Arrival time:     " + arrivalTime);
			System.out.println("Burst time:       " + burstTime);
			System.out.println("Priority:         " + priority);
			System.out.println("Remaining Time:   " + remainingTime);
			System.out.println("Waiting time:     " + waitingTime);
			System.out.println("Turnaround time:  " + turnaroundTime);
			System.out.println();

		}

		private boolean ageNow() {
			return this.waitingTime % ageingTime == 0;
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
			Scanner s = new Scanner(System.in);

			if (n > 0)
				System.out.println("Please enter Arrival time , Burst time and Priority for each process");
			for (int i = 0; i < n; i++)
				p.add(new Process(s.nextInt(), s.nextInt(), s.nextInt()));
			s.close();
		}

		public ArrayList<Process> sortArrival() {
			int n = p.size();
			for (int i = 0; i < n; i++) {
				for (int j = n - 1; j > 0; j--) {
					if (p.get(j).arrivalTime < p.get(j - 1).arrivalTime) {

						Process temp = p.get(j);
						p.set(j, p.get(j - 1));
						p.set(j - 1, temp);
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

		public void addWaitingTime() {
			// since we only execute of rq.get(0)
			// just increment the rest
			for (int i = 1; i < this.size(); i++)
				this.p.get(i).waitingTime++;

		}

		private void age() {

			for (Process p : this.p) {
				if (p.ageNow() && p.priority > 1)
					p.priority--;
			}
		}

		private boolean empty() {
			return this.p.size() == 0;
		}

		private Process get(int i) {
			if (this.p.size() == 0)
				return null;

			return this.p.get(i);
		}
	}

	public static void activate() {

		Priority h = new Priority();
		Scanner scan = new Scanner(System.in);

		System.out.println("Number of processes");
		int n = scan.nextInt();

		ProcessList allProcesses = h.new ProcessList(n);
		System.out.println("Sorted input:\n");
		allProcesses.sortArrival();
		allProcesses.printList();

		ProcessList rq = h.new ProcessList(0);

		h.beginExecution(allProcesses, rq);

		for (Process pr : finishedProcesses)
			pr.printProcess();

		scan.close();
	}

	public void beginExecution(ProcessList all, ProcessList rq) {

		while (all.size() > 0 || rq.size() > 0) {

			addToReadyQueue(all, rq);
			Process temp = rq.get(0); // highest p

			if (temp == null) {
				currTime++;
				continue;
			} else {

				currTime++;
				temp.execute();
				rq.addWaitingTime();

				if (temp.remainingTime == 0) // so I deleted this object
					System.out.println("Removing process from readyQueue " + rq.p.remove(temp) + "\n");

			}

			if (rq.size() > 0) {
				rq.age();
			}

		}
	}

	private void insertByPriority(ProcessList rq, Process temp) {

		System.out.println("Inserting by priority\n");
		if (rq.empty()) {
			rq.p.add(temp);

		} else {
			int i = 0;
			while ((temp.priority >= rq.p.get(i).priority) && i < rq.size() ) {
				System.out.println("Switching");
				System.out.println("While loop:   i=" + i);
				i++;
			}
			rq.p.add(i, temp);
		}
	}

	private void addToReadyQueue(ProcessList all, ProcessList ready) {

		// this is just to add the next elements after each time increment

		while (all.size() != 0 && all.p.get(0).arrivalTime <= currTime) {

			System.out.println("Process appended at t=" + currTime);
			Process temp = all.p.get(0);
			insertByPriority(ready, temp); 
			all.p.remove(0);

		}

	}

	public static void main(String[] args) {
		Priority priority = new Priority();
		priority.activate();

	}

}
