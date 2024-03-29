﻿# TSP-GeneticAlgorithim
 
This project implements a genetic algorithm in Java to solve the Traveling Salesman Problem (TSP). The TSP is a combinatorial optimization problem where the goal is to find the shortest possible route that visits each city exactly once and returns to the origin city.

Overview:
The tspTest class serves as the main entry point for the genetic algorithm implementation. It includes methods for generating an initial population of candidate solutions, evaluating the fitness of each solution, selecting parents for crossover based on their fitness, and applying crossover and mutation operations to generate new offspring solutions.

Key Features:
Population Generation: The algorithm starts by generating a random population of candidate solutions, each represented as a permutation of cities.
Fitness Evaluation: The fitness of each solution is evaluated based on the total distance traveled in the solution route, considering the Euclidean distance between consecutive cities.
Parent Selection: The algorithm implements various selection methods, including rank-based selection and roulette wheel selection, to choose parents for crossover based on their fitness values.
Crossover and Mutation: Crossover operations are applied to selected parent solutions to produce offspring solutions, while mutation operations introduce small random changes to the offspring solutions to maintain diversity in the population.
Convergence Criteria: The algorithm includes convergence criteria to terminate the optimization process when there is no improvement in the best solution for a predefined number of iterations.

Usage:
To use this project, you need a dataset containing the coordinates of cities. The provided berlin52.tsp dataset is an example dataset with 52 cities in Berlin, Germany. You can replace it with your own dataset following the same format.

Instructions:
Compile the Java code: javac tspTest.java
Run the program: java tspTest

Results:
The program outputs the best fitness value for each iteration, along with statistics such as the worst fitness, average fitness, and standard deviation. It continues running until a convergence criterion is met or a maximum number of iterations is reached.
