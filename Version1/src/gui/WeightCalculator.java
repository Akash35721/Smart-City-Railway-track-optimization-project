package src.gui;

public class WeightCalculator {
	public static double calculateWeight(int populationDensity, int economicFactor, int terrainFactor, int disasterRisk, int strategicImportance, boolean isPriority) {
		double weight = 0.0;
		weight += populationDensity * 0.2;
		weight += economicFactor * 0.2;
		weight += terrainFactor * 0.1;
		weight += disasterRisk * 0.3;
		weight += strategicImportance * 0.2;
		weight *= isPriority ? 0.8 : 1.0; // Priority reduces the weight
		return weight;
	}
}
