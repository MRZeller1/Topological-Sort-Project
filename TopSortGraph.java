


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Iterator;

public class TopSortGraph {
	// Vertex class where the vertex is a linked list and has an indegree for topsort
	private static class Vertex {
		// Vertex contains the Vetexes name as a String, a LinkedList of String for its out edges,
		// and an integer representing the amount of edges it has
		private String name;
		private LinkedList<String> edges;
		private int indegree = 0;
		
		// constructor
		public Vertex(String name) {
			this.name = name;
			this.edges = new LinkedList<>();
		}
		
		// name getter
		public String getName() {
			return this.name;
		}
		// Indegree getter
		private int getIndegree() {
			return this.indegree;
		}
		
		// Indegree incrementer and Decrementer and setter methods
		private void incrementIndegree() {
			this.indegree++;
		}
		// Overloaded to add a given amount
		private void incrementIndegree(int n) {
			this.indegree += n;
		}
		
		private void decrementIndegree() {
			this.indegree--;
		}
		
		private void setIndegree(int n) {
			this.indegree = n;
		}
		//Helper method to count occurrences of an edge for a given vertex for indegrees
		public int countEdge(String name) {
			
			int count = 0;
			Iterator<String> current = edges.iterator();
			// Loop to add all occurrences of the edge and return the integer
			while(current.hasNext()) {
				if(current.next().equals(name)) {
					count++;
				}
			}
			return count;
		}
		
		
		
	}
	
	private static class Graph {
		// Data structure is an ArrayList of Linked List to maintain size, which is the purpose of an adjacency list, and the vertexes are indexed
		private ArrayList<Vertex> vertices = new ArrayList<>();
		public int size = 0;
		
		//getter to return the vertex with the name as an argument
		public Vertex getVertex(String name){
			for (Vertex vertex : vertices) {
				if(vertex.getName().equals(name)) {
					return vertex;
				}
			}
			return null;
		}
		// Overloaded getter to return the Vertex with the index passed
		public Vertex getVertex(int i) {
			return vertices.get(i);
		}
		
		// Add edge adds each edge and if its corresponding vertex already exists in the graph, then increments that vertices indegree
		public void addEdge(int index, String name) {
			if (index < 0 || index >= size()) return;
			// Adds edge
			getVertex(index).edges.add(name);
			// increments corresponding indegree if applicable
			Vertex toVertex = getVertex(name);
			if(toVertex != null) {
				toVertex.incrementIndegree();
			}
		}
		
		// Method to add a vertex and update the indegrees of the edges added along with the vertex
		public void addVertex(Vertex vertex) {
			String name = vertex.name;
			if(!vertices.isEmpty()) {
				// First finds all edges of the vertex that were added to the graph before this vertex and increments the indegree
				for(int i = 0; i < size(); i++) {
					vertex.incrementIndegree(getVertex(i).countEdge(name));
				}
			}
			// Simply adds the vertex to the ArrayList
			vertices.add(vertex);
		}
		
		// Returns the amount of vertices in the graph
		public int size(){
			return vertices.size();
		}
		
	
		// Method to reset the vertices indegrees after topsort is complete (Maintain graphs correctness)
		private void resetIndegree() {
			// Sets each indegree to zero
			for(int i = 0; i < size(); i++) {
				getVertex(i).setIndegree(0);
			}
			// Goes through each edge and increments indegrees accordingly 
			for(int i = 0; i < size(); i++) {
				Iterator<String> edge = getVertex(i).edges.iterator();
				while(edge.hasNext()) {
					getVertex(edge.next()).incrementIndegree(); 
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Graph graph = new Graph();		// First the graph is created
		createAdjacentcyList(graph);	// Now we use our create adjacency list method to read in a file and fill the graph

		int size = graph.size();
		String[] topologicalList = new String[size];	// Array for the resulting topological sorted list

		topsort(graph, topologicalList);	// topsort method to sort list in topological order

		// Loop to print the result
		for(int i = 0; i < size; i++) {
			System.out.print(topologicalList[i] + " ");
		}
		
	}
	
	public static void topsort(Graph graph, String[] topologicalList) {
			// Queue for the vertexes, by indegree
			Queue <Vertex> q= new LinkedList<>();
			int counter = 0;
			// Insert each vertex with no Indegree into the queue
			for(Vertex v : graph.vertices) {
				if(v.indegree == 0) {
					q.add(v);
				}
			}
			
			//Queue should be empty after each vertex is iterated through, if not then a Cycle was in the graph and a vertex got added twice
			while(!q.isEmpty()) {
				// Append the Indegree at the end of the queue to the Sorted List
				Vertex v = q.remove();
				topologicalList[counter++] = v.name;
				// Loop through each edge and appending the edges with indegrees equal to zero to the queue
				// (neglecting the current vertex, v's, and edge's that is connected to it)
				Iterator<String> w = v.edges.iterator();
				while(w.hasNext()) {
					String edge = w.next();
					if(--graph.getVertex(edge).indegree == 0) {
						q.add(graph.getVertex(edge));
					}
				}	
			}
			// exception not found if a cycle is detected (If the topological list is larger than the amount of vertices)
			graph.resetIndegree(); // Resets the graphs indegree to maintain the graph
			if(counter != graph.size()) {
				throw new RuntimeException("Cycle detected in the graph");
			}
	}
	
	public static void createAdjacentcyList(Graph graph) {
		// Scanner to read in file until EOF
		Scanner in = new Scanner(System.in);
		int j = 0;
		while(in.hasNext()) {
			// reads the current line
			// Splits it and places each String into elements of the Array
			String line = in.nextLine();
			String[] lineArray = line.split(" ");
			Vertex vertex = new Vertex(lineArray[0]);
			// Adding the first String as a vertex
			// Adding each edge to the linked list
			graph.addVertex(vertex);
			for(int i = 1; i < lineArray.length; i++) {
				//If the vertex is equaled to one of its edges a cycle-detected error will be thrown
				if(lineArray[i].equals(lineArray[0]))
					throw new RuntimeException("Cycle detected in the graph");
				graph.addEdge(j, lineArray[i]);
			}
			j++;
		}
        in.close();
	}
}
