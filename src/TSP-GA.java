import java.io.*;
import java.util.*;

public class tspTest {
    static class City {
        private double x;
        private double y;
        private int cityIndex;

        public City(double x, double y, int cityIndex) {
            this.x = x;
            this.y = y;
            this.cityIndex = cityIndex;
        }

        public double getY() {
            return y;
        }

        public double getX() {
            return x;
        }

        public int getCityIndex() {
            return cityIndex;
        }
    }

    // A chromosome is a object with a list of cities and a fitness
    // [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ] -> a part chromosome
    // 2374.234 -> fitness is a part of a chromosome
    // [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ] , 2374.234 -> a chromosome

    class Chromosome {
        private final ArrayList<City> cities;
        private double fitness;
        private double selectionProbability;

        public Chromosome(ArrayList<City> cities) {
            this.cities = cities;
            this.fitness = 0;
            this.selectionProbability = 0;
        }

        public double getSelectionProbability() {
            return selectionProbability;
        }

        public void setSelectionProbability(double selectionProbability) {
            this.selectionProbability = selectionProbability;
        }

        public ArrayList<City> getCities() {
            return cities;
        }

        public double getFitness() {
            return fitness;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }
    }

    public static double calculateDistance(City city1, City city2) {
        double x1 = city1.getX();
        double y1 = city1.getY();
        double x2 = city2.getX();
        double y2 = city2.getY();

        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        return distance;
    }

    // calculate the fitness of a chromosome in a closed loop
    public static double calculateFitness(ArrayList<City> cities) {
        double fitness = 0;
        double totalDistance = 0;


        for (int i = 0; i < cities.size(); i++) {
            City city1 = cities.get(i);
            City city2 = cities.get((i + 1) % cities.size()); // Modulo operation for circular traversal
            totalDistance += calculateDistance(city1, city2);
        }

        // add the distance from the last city to the first city
        totalDistance += calculateDistance(cities.get(cities.size() - 1), cities.get(0));

        // inverse of fitness is the fitness, also to prevent divison by 0
        fitness = 1 / totalDistance;

        return fitness;
    }

    public List<Chromosome> generateRandomPopulation(ArrayList<City> cities, int populationSize) {
        List<Chromosome> population = new ArrayList<Chromosome>();

        for (int i = 0; i < populationSize; i++) {
            ArrayList<City> chromosome = new ArrayList<City>(cities);
            Collections.shuffle(chromosome);
            population.add(new Chromosome(chromosome));
        }

        return population;
    }

    public void evaluateFitness(List<Chromosome> population) {
        for (Chromosome chromosome : population) {
            chromosome.setFitness(calculateFitness(chromosome.getCities()));
        }
    }


    public Chromosome selectElite(List<Chromosome> population) {
        //initialize our best individual to first chromosome
        Chromosome bestIndividual = population.get(0);
        //loop through the population and assign each solution to temp until best fitness found
        for (int i = 0; i < population.size(); i++) {
            Chromosome TempChromosome = population.get(i);
            if (TempChromosome.getFitness() > bestIndividual.getFitness()) {
                bestIndividual = TempChromosome; //each time will update the best individual till it finds best one
            }
        }
        return bestIndividual;
    }

    public List<Chromosome> CreateNewGeneration(List<Chromosome> population) {
        List<Chromosome> newGeneration = new ArrayList<>();//create a new generation

        Chromosome bestIndividual = selectElite(population);//get best individual from method and assign
        newGeneration.add(bestIndividual);//add only the best individual to my new generation
        return newGeneration;
    }


    public static Chromosome selectRank(List<Chromosome> population) {
        // Sort the population by fitness in descending order
        population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());

        // Assign selection probabilities based on rank
        double totalProbability = 0.0;
        for (int i = 0; i < population.size(); i++) {
            double probability = (double) (i + 1) / population.size();
            population.get(i).setSelectionProbability(probability);
            totalProbability += probability;
        }

        // Select based on the assigned probabilities
        Random random = new Random();
        double randomProbability = random.nextDouble() * totalProbability;

        double cumulativeProbability = 0.0;
        for (Chromosome chromosome : population) {
            cumulativeProbability += chromosome.getSelectionProbability();
            if (cumulativeProbability >= randomProbability) {
                return chromosome;
            }
        }

        return population.get(population.size() - 1); // Return last chromosome if not selected
    }

    public static Chromosome selectRoulette(List<Chromosome> population) {
        double totalFitness = 0.0;
        for (Chromosome chromosome : population) {
            totalFitness += chromosome.getFitness();
        }

        Random random = new Random();
        double randomFitness = random.nextDouble() * totalFitness;

        double additiveFitness = 0.0;
        for (Chromosome chromosome : population) {
            additiveFitness += chromosome.getFitness();
            if (additiveFitness >= randomFitness) {
                return chromosome;
            }
        }
        return population.get(population.size() - 1); // Return last chromosome if not selected
    }

    public static List<Chromosome> ParentSelection(List<Chromosome> population) {
        List<Chromosome> selectedParents = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < population.size(); i++) {
            double randomGenerator = random.nextDouble();

            // 50% chance of using rank-based selection
            if (randomGenerator < 0.5) {
                selectedParents.add(selectRank(population));
            } else { // 50% chance of using roulette wheel selection
                selectedParents.add(selectRoulette(population));
            }
        }

        return selectedParents;
    }

    public List<Chromosome> Crossover(List<Chromosome> parents) {
        List<Chromosome> offspring = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < parents.size(); i += 2) {
            if (i + 1 < parents.size()) {
                Chromosome parent1 = parents.get(i);
                Chromosome parent2 = parents.get(i + 1);

                ArrayList<City> parent1Cities = parent1.getCities();
                ArrayList<City> parent2Cities = parent2.getCities();

                int chromosomeLength = parent1Cities.size();

                int startPos = random.nextInt(chromosomeLength);
                int endPos = random.nextInt(chromosomeLength - startPos) + startPos;

                List<City> child1 = new ArrayList<>(Collections.nCopies(chromosomeLength, null));
                List<City> child2 = new ArrayList<>(Collections.nCopies(chromosomeLength, null));

                for (int j = startPos; j <= endPos; j++) {
                    child1.set(j, parent1Cities.get(j));
                    child2.set(j, parent2Cities.get(j));
                }

                int index1 = endPos + 1;
                int index2 = endPos + 1;

                for (int j = endPos + 1; j < chromosomeLength + endPos + 1; j++) {
                    int index = j % chromosomeLength;

                    if (!child1.contains(parent2Cities.get(index))) {
                        while (child1.get(index1 % chromosomeLength) != null) {
                            index1++;
                        }
                        child1.set(index1 % chromosomeLength, parent2Cities.get(index));
                    }

                    if (!child2.contains(parent1Cities.get(index))) {
                        while (child2.get(index2 % chromosomeLength) != null) {
                            index2++;
                        }
                        child2.set(index2 % chromosomeLength, parent1Cities.get(index));
                    }
                }

                offspring.add(new Chromosome(new ArrayList<>(child1)));
                offspring.add(new Chromosome(new ArrayList<>(child2)));


            }
        }
        // Calculate fitness for offspring
        for (Chromosome child : offspring) {
            child.setFitness(calculateFitness(child.getCities()));
        }

        return offspring;
    }


    public void InsertMutation(Chromosome chromosome) {
        //we want to get list of cities from our chromosome object
        ArrayList<City> cities = chromosome.getCities();

        //create a random object to give us random numbers
        Random random = new Random();

        //now generate two random numbers that is within out city size
        int p1 = random.nextInt(cities.size());
        int p2 = random.nextInt(cities.size());

        //we can swap the cities at the index that is randomly generation
        Collections.swap(cities, p1, p2);
    }


    public void randomSlide(Chromosome chromosome) {
        ArrayList<City> cities = chromosome.getCities();
        Random random = new Random();

        int startIndex = random.nextInt(cities.size());
        int sectionSize = random.nextInt(cities.size() - startIndex); // Adjusted section size




        // which section to be moved
        List<City> section = new ArrayList<>(cities.subList(startIndex, startIndex + sectionSize));
        cities.removeAll(section);

        // Randomly insert the segment at a different position
        int insertIndex = random.nextInt(cities.size() + 1);
        cities.addAll(insertIndex, section);
    }


    public List<Chromosome> selectMutation(List<Chromosome> offspring) {
        Random random = new Random();
        //generate random num between 0-1 to choose our selection choice
        List<Chromosome> MutatedOffspring = new ArrayList<>();
        for (Chromosome chromosome : offspring) {
            double RandomGen = random.nextDouble();

            if (RandomGen > 0.5) {
                InsertMutation(chromosome);

            } else {
                randomSlide(chromosome);
            }
            MutatedOffspring.add(chromosome);

        }

        return MutatedOffspring;


    }


    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("src\\berlin52.tsp");
        Scanner scan = new Scanner(file);

        ArrayList<City> Cities = new ArrayList<City>();
        boolean foundSection = false;

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.equals("NODE_COORD_SECTION")) {
                foundSection = true;
                continue; // Start reading coordinates
            }

            if (foundSection) {
                if (line.equals("EOF")) {
                    break; // End of file reached
                }

                Scanner lineScanner = new Scanner(line);
                int cityIndex = lineScanner.nextInt();
                double x = lineScanner.nextDouble();
                double y = lineScanner.nextDouble();
                Cities.add(new City(x, y, cityIndex));
                lineScanner.close();
            }
        }

        scan.close();

        tspTest tsp = new tspTest();
        // generate a random population
        List<Chromosome> initialPop = tsp.generateRandomPopulation(Cities, 100);
        // evaluate fitness
        tsp.evaluateFitness(initialPop);



        //perform all functions for 100 iterations
        int max_Iter = 100;

        int unchangedCount = 0;

//
////Test parent selection
//        List<Chromosome> selectedParents = tsp.ParentSelection(initialPop);
//        System.out.println("Selected Parents:");
//        for (Chromosome parent : selectedParents) {
//            System.out.println("Fitness: " + parent.getFitness());
//        }
//
//        //Test crossover
//        List<Chromosome> crossovered = tsp.Crossover(selectedParents);
//        System.out.println("Crossovered Offspring:");
//        for (Chromosome offspring : crossovered) {
//            System.out.println("Fitness: " + offspring.getFitness());
//        }


        for (int i = 1; i <= max_Iter; i++) {

            // find the individual with the best fitness
            Chromosome bestSolution = tsp.selectElite(initialPop);

            //create List to store best solution in first generation and add to elitistPop
            List<Chromosome> elitistPop = new ArrayList<>();
            elitistPop.add(bestSolution);

            // Remove the elitist from the initial population
            initialPop.remove(bestSolution);

            List<Chromosome> parentsSelected = tsp.ParentSelection(initialPop);
            List<Chromosome> crossPop = tsp.Crossover(parentsSelected);
            List<Chromosome> mutatePop = tsp.selectMutation(crossPop);

           //new gen to store mutatedPop and Elitist
            List<Chromosome> newGen = new ArrayList<>();
            newGen.addAll(mutatePop);
            newGen.addAll(elitistPop);

            //sort the new generation in descending order
            Collections.sort(newGen, Comparator.comparing(Chromosome::getFitness).reversed());

            // The best chromosome will be the first one after sorting
            Chromosome bestChromosome = newGen.get(0);
            double currentBestFitness = bestChromosome.getFitness();

            // Print the best fitness for each cycle
            System.out.println("Iteration " + i + " - Best Fitness: " + currentBestFitness);
            System.out.println("Iteration " + i + " - Worst Fitness: " + newGen.get(newGen.size()-1).getFitness());
            System.out.println("Iteration " + i + " - Average Fitness: " + newGen.stream().mapToDouble(Chromosome::getFitness).average().getAsDouble());
            System.out.println("Iteration " + i + " - Standard Deviation: " + Math.sqrt(newGen.stream().mapToDouble(Chromosome::getFitness).map(x -> Math.pow(x - newGen.stream().mapToDouble(Chromosome::getFitness).average().getAsDouble(), 2)).sum() / newGen.size()));
            System.out.println(("-------------------------------------------------------------------"));

            //update new pop
            initialPop=newGen;


            //convergence criteria
            if (bestSolution == bestChromosome) {
                unchangedCount++;
            } else {
                unchangedCount = 0;
            }

            if (unchangedCount == 50) {
                break;
            }



        }


    }

}
