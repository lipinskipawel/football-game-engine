package io.lipinski.player.ai.internal;

import io.lipinski.player.ai.internal.activation.ActivationFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SimpleNeuralNetwork implements NeuralNetwork {

    final List<Matrix> nodes;
    final List<Matrix> biases;

    private final ActivationFunction activationFunction;
    private final double learningRate;


    private SimpleNeuralNetwork(final int[] architecture,
                                final ActivationFunction activation,
                                final double learningRate) {
        this.nodes = new ArrayList<>(architecture.length);
        this.biases = new ArrayList<>(architecture.length);

        for (int i = 0; i < architecture.length; i++) {
            if (i == 0)
                this.nodes.add(Matrix.of(architecture[i], architecture[i]));
            else
                this.nodes.add(Matrix.of(architecture[i], architecture[i - 1]));
            this.biases.add(Matrix.of(architecture[i], 1));
        }
        this.activationFunction = activation;
        this.learningRate = learningRate;
        randomize();
    }

    SimpleNeuralNetwork(final List<Matrix> weights,
                        final List<Matrix> biases,
                        final ActivationFunction activation,
                        final double learningRate) {
        this.nodes = new ArrayList<>(weights);
        this.biases = new ArrayList<>(biases);
        this.activationFunction = activation;
        this.learningRate = learningRate;
    }

    static NeuralNetwork factory(final DeepNeuralNetwork factory) {
        return new SimpleNeuralNetwork(factory.architecture, factory.activation, factory.learningRate);
    }

    @Override
    public Result predict(final Matrix data) {
        final Matrix output = feedForwardInternal(data);
        final var doubles = Arrays.stream(output
                .rawData())
                .mapToDouble(raw -> raw[0])
                .toArray();
        return Result.of(Arrays.stream(doubles).boxed().toArray(Double[]::new));
    }

    @Override
    public Result predict(final int[] data) {
        return predict(Matrix.of(data));
    }

    @Override
    public Result predict(final double[] data) {
        return predict(Matrix.of(data));
    }

    @Override
    public void train(final Matrix data, final Matrix labels) {
        for (var temp : data.transpose().rawData()) {
            var oneColumnOfData = Matrix.of(temp);
            final var outputOnLayers = new ArrayList<Matrix>();

            for (int i = 0; i < this.nodes.size(); i++) {
                final var weight = this.nodes.get(i);
                final var bias = this.biases.get(i);
                var tempData = oneColumnOfData;
                if (i != 0)
                    tempData = outputOnLayers.get(i - 1);
                final var compute = activationFunction
                        .compute(weight
                                .multiply(tempData)
                                .add(bias)
                        );
                outputOnLayers.add(compute);
            }
            // var computedErrors = outputErrors, hiddenErrors, secondHidden....
            var computedErrors = new ArrayList<Matrix>();
            var valuesToDeltas = computeDeltas(oneColumnOfData, outputOnLayers);

            var j = 0;
            for (int i = this.nodes.size() - 1; i >= 0; i--) {
                final var outputErrorComputed = computeError(i, labels, outputOnLayers.get(outputOnLayers.size() - 1), computedErrors, j - 1);
                computedErrors.add(outputErrorComputed);

                final var matrix = outputOnLayers.get(i);
                final var gradient = activationFunction.derivative(matrix)
                        .multiply(computedErrors.get(j))
                        .forEach(x -> x * learningRate);
                final var deltaaa = gradient.multiply(valuesToDeltas.get(i));

                this.nodes.set(i, this.nodes.get(i).add(deltaaa));
                this.biases.set(i, this.biases.get(i).add(gradient));
                j++;
            }
        }
    }

    @Override
    public void train(final int[] data, final int labels) {
        train(Matrix.of(data), Matrix.of(labels));
    }

    private Matrix computeError(final int index,
                                final Matrix labels,
                                final Matrix outputs,
                                final List<Matrix> computedErrors,
                                final int j) {
        if (index == this.nodes.size() - 1) {
            return labels.subtract(outputs); // 1x1
        }
        final var who_t = this.nodes.get(index + 1).transpose(); // 2x4
        return who_t.multiply(computedErrors.get(j)); // wczesniej bylo 0 i dzialala
    }

    // data.T(input data), hidden.T(first output from FF), secondHidden.T(second output from FF)
    private List<Matrix> computeDeltas(final Matrix data,
                                       final List<Matrix> outputOnLayers) {
        final var deltas = new ArrayList<Matrix>();
        deltas.add(data.transpose());
        for (Matrix some : outputOnLayers) {
            deltas.add(some.transpose());
        }
        return deltas;
    }

    private Matrix feedForwardInternal(final Matrix data) {
        if (this.nodes.get(0).rawData()[0].length != data.numberOfRows())
            throw new InvalidInputFormatException("Shape of input " + data.numberOfRows()
                    + " must be the same as input for neural network " + this.nodes.get(0).numberOfRows());
        final var outputOnLayers = new ArrayList<Matrix>();

        for (int i = 0; i < this.nodes.size(); i++) {
            final var weight = this.nodes.get(i);
            final var bias = this.biases.get(i);
            var tempData = data;
            if (i != 0)
                tempData = outputOnLayers.get(i - 1);
            final var compute = activationFunction.compute(weight
                    .multiply(tempData)
                    .add(bias));
            outputOnLayers.add(compute);
        }
        return outputOnLayers.get(outputOnLayers.size() - 1);
    }

    private void randomize() {
        for (int i = 0; i < this.nodes.size(); i++) {
            this.nodes.set(i, this.nodes.get(i).forEach(x -> Math.random() - .5));
            this.biases.set(i, this.biases.get(i).forEach(x -> Math.random() - .5));
        }
    }
}
