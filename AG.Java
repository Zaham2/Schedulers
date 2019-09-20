import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class AG {

	public static int currTime;
	public static ArrayList<Process> finishedProcesses;
	public static ProcessList allProcesses;
	public static ProcessList rqRT;
	public static ProcessList rqPriority;
	public static ProcessList rqArrival;

	// these are the utility & reused methods
	public AG() {

		Scanner scan = new Scanner(System.in);
		System.out.println("Number of processes");
		allProcesses = new ProcessList(scan.nextInt());
		scan.close();

		currTime = 0;
		finishedProcesses = new ArrayList<Process>();
		rqRT = new ProcessList(0);
		rqPriority = new ProcessList(0);
		rqArrival = new ProcessList(0);

	}

	public static void addToFinished(Process pr) {
		finishedProcesses.add(pr);
	}

	public static void updateRQs() {

		while (allProcesses.size() != 0 && allProcesses.p.get(0).arrivalTime <= currTime) {

			System.out.println("Process appended at t=" + currTime);
			Process temp = allProcesses.p.get(0);
			rqArrival.p.add(temp);
			rqPriority.insertByPriority(temp);
			rqRT.insertByRT(temp);
			allProcesses.p.remove(0);
		}
	}

	public static void removeFromQueues(Process pr) {

		rqArrival.p.remove(pr);
		rqPriority.p.remove(pr);
		rqRT.p.remove(pr);

	}

	class Process {

		int arrivalTime;
		int burstTime;
		int waitingTime;
		int remainingTime;
		int turnaroundTime;
		int priority;
		int quantum;
		int staticQuantum;
		int switchTime;

		ArrayList<Integer> qList;

		public Process(int a, int b, int p, int q) {
			arrivalTime = a;
			burstTime = b;
			priority = p;
			quantum = q;
			staticQuantum = q;

			remainingTime = b;
			waitingTime = 0;
			turnaroundTime = 0;
			switchTime = 0;

			qList = new ArrayList<Integer>();
		}

		public Process(Process proc) {
			arrivalTime = proc.arrivalTime;
			burstTime = proc.burstTime;
			this.quantum = proc.quantum;
			staticQuantum = proc.staticQuantum;

			remainingTime = proc.burstTime;
			priority = proc.priority;
			waitingTime = proc.waitingTime;
			turnaroundTime = proc.turnaroundTime;
			switchTime = 0;
		}

		public void printProcess() {
			System.out.println();
			System.out.println("Arrival time:     " + arrivalTime);
			System.out.println("Burst time:       " + burstTime);
			System.out.println("Priority:         " + priority);
			System.out.println("Quantum           " + quantum);
			System.out.println("Remaining Time:   " + remainingTime);
			System.out.println("Waiting time:     " + waitingTime);
			System.out.println("Turnaround time:  " + turnaroundTime);

			System.out.println("Quantum History");
			for (int i : qList)
				System.out.print(i + " ");
			System.out.println();

		}

		public void executeOnce() {

			this.remainingTime--;
			this.quantum--;

			this.waitingTime--;
			addWaiting();

			if (this.remainingTime == 0) {
				this.getTurnaround();
				this.getWaiting();
			}

		}

		public void addWaiting() {

			for (Process pr : rqArrival.p)
				pr.waitingTime++;
		}

		public void getWaiting() {
			this.waitingTime = (this.turnaroundTime - this.burstTime - this.switchTime);
		}

		public void getTurnaround() {
			this.turnaroundTime = currTime - this.arrivalTime + this.switchTime;
		}

		public boolean checkFinished() {

			if (this.remainingTime == 0)
				return true;

			else
				return false;
		}

		public void updateQList() {
			
			if(qList.size() >0 && qList.get(qList.size() -1) == this.quantum)
				return;	//this just avoids duplication
			
			System.out.println("Added " + this.quantum + " to qList of Process " + this.arrivalTime);
			this.qList.add(this.quantum);
		}

		public void terminate() {

			this.quantum = 0;
			this.updateQList();
			addToFinished(this);
			removeFromQueues(this);
		}

		public void addQuantum2() {
			this.staticQuantum += 2;
			this.quantum = this.staticQuantum;
		}

		public void addQuantum2Ceil() {

			this.staticQuantum += (int) Math.ceil((double) this.quantum / 2);
			this.quantum = this.staticQuantum;
		}

		public void addQuantumRemaining() {
			this.staticQuantum += this.quantum; // first add remaining
			this.quantum = this.staticQuantum; // now set
		}

		public void contextSwitchDelay() {

			// this delay is added to a Process
			// That has been replaced
			// from its running state

			// The other process that takes
			// this one's place
			// gets +1 waitingTime (along with
			// all other Processes in rq)
			System.out.println("Context switch of process " + this.arrivalTime);
			addTime();

			this.waitingTime--;
			this.addWaiting(); // add waiting for all except this one in queue

			this.switchTime++;
		}

		public int calct3() {
			return this.quantum - (2 * (int) Math.ceil((double) this.quantum / 4));
		}

		public int calct() {
			return (int) Math.ceil((double) this.quantum / 4);
		}
	}

	class ProcessList {

		ArrayList<Process> p;

		public ProcessList() {

		}

		public ProcessList(int n) {

			p = new ArrayList<Process>(n);

			if (n > 0)
				System.out.println("Please enter Arrival time ,Burst time, Priority and quantum for each process");

			Scanner s = new Scanner(System.in);
			for (int i = 0; i < n; i++)
				p.add(new Process(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt()));
			s.close();
		}

		public ProcessList sortArrival() {
			int n = p.size();
			for (int i = 0; i < n; i++) {
				for (int j = n - 1; j > 0; j--) {
					if (p.get(j).arrivalTime < p.get(j - 1).arrivalTime) {
						swap(p.get(j), p.get(j - 1));
					}

				}
			}
			return this;

		}

		public ProcessList sortPriority() {
			int n = p.size();
			for (int i = 0; i < n; i++) {
				for (int j = n - 1; j > 0; j--) {
					if (p.get(j).priority < p.get(j - 1).priority) {
						swap(p.get(j), p.get(j - 1));
					}

				}
			}
			return this;

		}

		public ProcessList sortRT() {
			int n = p.size();
			for (int i = 0; i < n; i++) {
				for (int j = n - 1; j > 0; j--) {
					if (p.get(j).remainingTime < p.get(j - 1).remainingTime) {
						swap(p.get(j), p.get(j - 1));
					}

				}
			}
			return this;

		}

		public void printList() {
			System.out.println();
			for (int i = 0; i < p.size(); i++) {
				System.out.println(p.get(i).arrivalTime + " " + p.get(i).burstTime);
			}
			System.out.println();
		}

		public int size() {
			return p.size();
		}

		public Process returnLowestRT() {
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

		public void insertByPriority(Process temp) {
			;
			if (this.size() == 0) {
				this.p.add(temp);

			} else {

				this.p.add(0, temp);
				for (int i = 1; i < this.size(); i++) {

					if (this.get(i).priority < temp.priority)
						Collections.swap(this.p, i, this.p.indexOf(temp));

				}
			}

		}

		public void swap(Process p1, Process p2) {

			Process temp = p1;
			p1 = p2;
			p2 = temp;
		}

		public void insertByRT(Process temp) {

			if (this.size() == 0) {
				this.p.add(temp);

			} else {

				this.p.add(0, temp);
				for (int i = 1; i < this.size(); i++) {

					if (this.get(i).remainingTime < temp.remainingTime)
						Collections.swap(this.p, i, this.p.indexOf(temp));

				}
			}

		}

		public Process get(int index) {
			if (this.p.size() == 0)
				return null;
			else
				return this.p.get(index);
		}

		public boolean empty() {
			return this.p.size() == 0;
		}

		public void reQueue(Process pr) {

			removeFromQueues(pr);
			rqArrival.add(pr);
			rqPriority.insertByPriority(pr);
			rqRT.insertByRT(pr);
		}

		public void add(Process pr) {
			this.p.add(pr);
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

		public Process getLowestPriority() {

			if (p.size() == 0) {

				System.out.println("The queue is empty\nReturning null");
				return null;

			} else if (p.size() == 1) {

				return p.get(0);

			} else {

				Process lowest = p.get(0);

				for (Process proc : p) {
					if (proc.priority < lowest.priority)
						lowest = proc;
				}

				return lowest;
			}
		}

	}

	public static void activate() {

		// allProcesses initialisation done is constructor
		// since we made allProcesses static...
		// Java defines it by default before execution
		// of this method...
		// So i have to define (instantiate) it in the
		// constructor... before any methods are run

		System.out.println("Sorted input:\n");
		allProcesses.sortArrival();
		allProcesses.printList();

		// Same goes for rqQrrival
		// Making it static saves a lot of work
		// but has to be done at the beginning

		beginExecution();

		for (Process pr : finishedProcesses)
			pr.printProcess();

	}

	public static void beginExecution() {

		// NOTES of maghrib
		// 1- update t3 after the switch in executeP()
		// 2- Do not update qList in constructor
		// do it at the start of while loop here

		outer: while (allProcesses.size() > 0 || rqArrival.size() > 0) {
			updateRQs();
			Process currP = rqArrival.get(0);

			if (currP == null) {
				addTime();
				continue outer;
			}
			System.out.println("******* LOOP");
			System.out.println("currTime :  " + currTime);
			System.out.println("currentQuantum:   " + currP.quantum);
			System.out.println("currP Rem time:   " + currP.remainingTime);

			currP.updateQList();

			int t1 = currP.calct();
			int t2 = t1;
			int t3 = currP.calct3();

			System.out.println("\nExecuting RR for Process " + currP.arrivalTime);
			System.out.println("Value of Quantum:   " + currP.quantum);
			System.out.println("Value of t1:  " + t1 + "\n");

			executeRR(currP, t1);
			updateRQs();
			if (currP.checkFinished())
				continue outer;

			System.out.println("\nFinished RR for Process " + currP.arrivalTime);
			System.out.println("RT for process " + currP.arrivalTime + " is   " + currP.remainingTime);
			System.out.println("Value of Quantum:   " + currP.quantum);

			// If I'm here.. that mean the main process hasn't finished its time
			// So now come the PQ

			Process lowestP = rqArrival.getLowestPriority();
			if (lowestP == null) {
				System.out.println("How am I a null?");
				continue outer;
			}

			System.out.println("\nExecuting P for Process " + currP.arrivalTime);
			System.out.println("Value of Quantum:   " + currP.quantum);
			System.out.println("Value of t2:  " + t2 + "\n");

			executeP(lowestP, t2);
			updateRQs();

			// Now I've executeed the process with lowest P
			// I want to check if if it was the same process

			if (lowestP.checkFinished()) {
				if (areSame(lowestP, currP)) {

					// lowestP.terminate();
					continue outer;

				} else { // finished but not same

					lowestP.contextSwitchDelay();
					rqArrival.reQueue(currP);
					currP.addQuantum2Ceil();
					currP.updateQList();
					
					continue outer;
				}

			} else { // NOT FINISHED

				if (areSame(lowestP, currP)) {

					// Just moving onto step 3
				} else {
					// Not Same Not Finished

					lowestP.contextSwitchDelay();
					rqArrival.reQueue(currP);
					currP.addQuantum2Ceil();
					currP.updateQList();
					currP = lowestP;

				}

			}

			System.out.println("\nFinished P for Process " + currP.arrivalTime);
			System.out.println("Value of Quantum:   " + currP.quantum);
			System.out.println("RT for process " + currP.arrivalTime + " is   " + currP.remainingTime);
			System.out.println("Value of t2:  " + t2 + "\n");

			Process lowestRT = rqArrival.getLowestRT();

			System.out.println("\nExecuting SJF for Process " + lowestRT.arrivalTime);
			System.out.println("Value of Quantum:   " + lowestRT.quantum);
			System.out.println("RT for process " + lowestRT.arrivalTime + " is   " + lowestRT.remainingTime);
			System.out.println("Value of t3:  " + t3 + "\n");

			executeRT(lowestRT, t3);
			updateRQs();

			if (lowestRT.checkFinished()) {
				if (areSame(lowestP, currP)) {
					System.out.println("I'm the same");
					System.out.println("I will do nothing");
				} else {
					System.out.println("I'm not the same, but I finished");
					System.out.println("I should reQueue currP after adding its rem qTime and updating its qList");

					// updated qList before RR on re-loop... remember... currP did NOT finish...
					// lowestRT is the one that finished
					currP.contextSwitchDelay();
					currP.addQuantumRemaining();
					currP.updateQList();
					rqArrival.reQueue(currP);

				}
			} else { // NOT FINISHED
				
				if (areSame(lowestP, currP)) {

					currP.addQuantum2();
					rqArrival.reQueue(currP);

				} else {

					// so... neither finished...
					// reQueue both... currP first of course... adding rem to it
					// reQueue the other... only restoring its quantum to its original? or add2?...
					// ADD 2!
					rqArrival.reQueue(currP);
					currP.addQuantumRemaining();
					currP.contextSwitchDelay();
					currP.updateQList();

					rqArrival.reQueue(lowestRT);
					lowestP.addQuantum2();

				}
			}

			System.out.println("\nFinished SJF for Process " + lowestRT.arrivalTime);
			System.out.println("Value of Quantum:   " + lowestRT.quantum);
			System.out.println("RT for process " + lowestRT.arrivalTime + " is   " + lowestRT.remainingTime);
			System.out.println("Value of t3:  " + t3 + "\n");

		}
	}

	// these are the methods of AG

	public static void executeRT(Process lowestRT, int t3) {

		lowestRT.updateQList();
		for (int counter = 0; counter < t3; counter++) {

			lowestRT.executeOnce();
			addTime();

			if (lowestRT.checkFinished()) {

				System.out.println("Process " + lowestRT.arrivalTime + " has terminated in executeP");
				lowestRT.terminate();
				return;
			}

		}
		lowestRT.updateQList();

	}

	public static void executeP(Process lowestP, int t2) {

		lowestP.updateQList();
		for (int counter = 0; counter < t2; counter++) {

			lowestP.executeOnce();
			addTime();

			if (lowestP.checkFinished()) {

				System.out.println("Process " + lowestP.arrivalTime + " has terminated in executeP");
				lowestP.terminate();
				return;
			}

		}
		lowestP.updateQList();

	}

	public static void executeRR(Process currP, int t1) {

		currP.updateQList();
		for (int c = 0; c < t1; c++) { // The RR loop
			currP.executeOnce();
			addTime();

			if (currP.checkFinished()) {
				System.out.println("Process " + currP.arrivalTime + " has terminated in executeRR\n");
				currP.terminate();
				// This will execute next process
				return;
			}
		}
		currP.updateQList();
	}

	public static void addTime() {
		currTime++;
	}

	public static boolean areSame(Process lowestP, Process currP) {
		return lowestP == currP;
	}

	public static void main(String[] args) {

		AG ag = new AG();
		ag.activate();

		/*
		 * Process p1 = ag.new Process(1, 2, 3, 4); Process p2 = ag.new Process(5, 6, 7,
		 * 8); Process p3 = ag.new Process(9, 10, 11, 12); Process p4 = ag.new
		 * Process(13, 14, 15, 16); Process p5 = ag.new Process(17, 18, 19, 20); Process
		 * p6 = ag.new Process(21, 22, 23, 24); Process p7 = ag.new Process(25, 26, 27,
		 * 28);
		 * 
		 * allProcesses.p.add(p3); allProcesses.p.add(p1); allProcesses.p.add(p4);
		 * allProcesses.p.add(p2); allProcesses.p.add(p7); allProcesses.p.add(p5);
		 * allProcesses.p.add(p6); allProcesses.p.add(p2);
		 * 
		 * currTime = 99999; allProcesses.sortArrival();
		 * 
		 * addToReadyQueue();
		 * 
		 * System.out.println("Arrival Q"); rqArrival.printList();
		 * 
		 * System.out.println();
		 * 
		 * System.out.println("Priority Q"); for (Process pr : rqPriority.p)
		 * System.out.println(pr.priority);
		 * 
		 * System.out.println();
		 * 
		 * System.out.println("RT Q"); for (Process pr : rqRT.p)
		 * System.out.println(pr.remainingTime);
		 */

	}

}
