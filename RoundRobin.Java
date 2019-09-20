import java.util.ArrayList;
import java.util.Scanner;

public class RoundRobin {

	static int currTime;
	static ArrayList<Process> finishedProcesses;
	static int quantum = 3;

	public RoundRobin() {
		currTime = 0;
		finishedProcesses = new ArrayList<Process>();
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

		public Process getWaiting() {
			this.waitingTime = (this.turnaroundTime - this.burstTime);
			return this;
		}

		public Process getTurnaround() {
			this.turnaroundTime = currTime - this.arrivalTime;
			return this;
		}

		public void execute() {

			int counter = 0;
			while (this.remainingTime > 0 && counter < quantum) {

				currTime++;
				this.remainingTime--;

				if (this.remainingTime == 0) {
					this.getTurnaround();
					this.getWaiting();
					finishedProcesses.add(this);
					return;
				}
				counter++;
			}
		}
	}

	class ProcessList {

		ArrayList<Process> p;

		public ProcessList(int n) {

			p = new ArrayList<Process>(n);
			Scanner s = new Scanner(System.in);

			if (n > 0)
				System.out.println("Please enter Arrival time and Burst time for each process");
			for (int i = 0; i < n; i++)
				p.add(new Process(s.nextInt(), s.nextInt()));
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

	}

	public static void main(String[] args) {

		RoundRobin h = new RoundRobin();
		Scanner scan = new Scanner(System.in);

		System.out.println("Number of processes");
		int n = scan.nextInt();

		ProcessList allProcesses = h.new ProcessList(n);
		System.out.println("Sorted input:\n");
		allProcesses.sortArrival();
		allProcesses.printList();

		ProcessList rq = h.new ProcessList(0);

		// now for the logic
		// what do I need here?
		// I need to:
		// 1- read the next process(es) into my rq
		// 2- execute the Processes in order (use Queue?)

		h.beginExecution(allProcesses, rq);

		for (Process pr : finishedProcesses)
			pr.printProcess();

	}

	public void beginExecution(ProcessList all, ProcessList rq) {

		while (all.size() > 0 || rq.size() > 0) {

			addToReadyQueue(all, rq);
			Process toExecute = rq.p.get(0);

			if (toExecute != null) {

				toExecute.execute();
				rq.p.remove(toExecute);

				if (toExecute.remainingTime > 0) {
					addToReadyQueue(all, rq);
					rq.p.add(toExecute);
				}

			} else {
				currTime++;
				continue;
			}
		}

	}
	/*
	 * addToReadyQueue(all, rq); // at t = 0 while (all.size() > 0 || rq.size() > 0)
	 * {
	 * 
	 * // ERROR HERE Process nextInQueue = rq.p.get(0); // I need to REQUEUE the
	 * current process // if it has executed (quantum) time // DO NOT do .get(0) //
	 * you need to remove the dequeue first
	 * 
	 * // in this case... I need to make execution // a CLASS method not a Process
	 * method // sooo.. execute(ProcessList) // get the next inqueue there // if it
	 * exceeds the quantum: // 1-make a temp process // 2-remove rq.p.get(0) AFTER
	 * ADDING TO READY QUEUE @t+=3 // 3- add temp to the queue
	 * 
	 * if (nextInQueue == null) { currTime++; continue; }
	 * 
	 * else {
	 * 
	 * } }
	 */

	/*
	 * @SuppressWarnings("unused") public boolean execute(ProcessList rq) { //
	 * returns true of a process has been removed Process inTurn = rq.p.get(0);
	 * 
	 * if (inTurn == null) { currTime++; return false; } // now we need to loop
	 * 'quantum' times // int counter = 0; while (inTurn.remainingTime > 0 &&
	 * counter < quantum) {
	 * 
	 * currTime++; inTurn.remainingTime--;
	 * 
	 * if (inTurn.remainingTime == 0) { inTurn.getTurnaround(); inTurn.getWaiting();
	 * finishedProcesses.add(inTurn); rq.p.remove(inTurn);
	 * System.out.println("Process finished and removed"); return true; }
	 * 
	 * counter++; }
	 * 
	 * // we'll remove the process anyway rq.p.remove(inTurn); return false;
	 * 
	 * }
	 */
	public static ProcessList addToReadyQueue(ProcessList all, ProcessList ready) {

		while (all.size() != 0 && all.p.get(0).arrivalTime <= currTime) {

			System.out.println("Process appended at t=" + currTime);
			Process temp = all.p.get(0);
			ready.p.add(temp);
			all.p.remove(0);

		}

		return ready;
	}

}
