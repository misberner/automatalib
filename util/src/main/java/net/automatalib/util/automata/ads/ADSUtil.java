package net.automatalib.util.automata.ads;

import net.automatalib.automata.ads.ADSNode;
import net.automatalib.automata.ads.impl.ADSSymbolNode;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Word;
import net.automatalib.words.WordBuilder;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class, that offers some operations revolving around adaptive distinguishing sequences.
 *
 * @author frohme
 */
public class ADSUtil {

	public static <S, I, O> int computeLength(final ADSNode<S, I, O> node) {
		if (node.isLeaf()) {
			return 0;
		}

		return (node.isLeaf() ? 0 : 1) + node.getChildren()
				.values()
				.stream()
				.mapToInt(ADSUtil::computeLength)
				.max()
				.getAsInt();
	}

	public static <S, I, O> int countSymbolNodes(final ADSNode<S, I, O> node) {
		if (node.isLeaf()) {
			return 0;
		}

		return (node.isLeaf() ? 0 : 1) + node.getChildren()
				.values()
				.stream()
				.mapToInt(ADSUtil::countSymbolNodes)
				.sum();
	}

	public static <S, I, O> Pair<ADSNode<S, I, O>, ADSNode<S, I, O>> buildFromTrace(final MealyMachine<S, I, ?, O> automaton,
																					final Word<I> trace,
																					final S state) {
		final Iterator<I> sequenceIter = trace.iterator();
		final I input = sequenceIter.next();
		final ADSNode<S, I, O> head = new ADSSymbolNode<>(null, input);

		ADSNode<S, I, O> tempADS = head;
		I tempInput = input;
		S tempState = state;

		while (sequenceIter.hasNext()) {
			final I nextInput = sequenceIter.next();
			final ADSNode<S, I, O> nextNode = new ADSSymbolNode<>(tempADS, nextInput);

			final O oldOutput = automaton.getOutput(tempState, tempInput);

			tempADS.getChildren().put(oldOutput, nextNode);

			tempADS = nextNode;
			tempState = automaton.getSuccessor(tempState, tempInput);
			tempInput = nextInput;
		}

		return new Pair<>(head, tempADS);
	}

	public static <S, I, O> Set<ADSNode<S, I, O>> collectFinalNodes(final ADSNode<S, I, O> root) {
		final Set<ADSNode<S, I, O>> result = new LinkedHashSet<>();
		collectFinalNodesRecursively(result, root);
		return result;
	}

	private static <S, I, O> void collectFinalNodesRecursively(final Set<ADSNode<S, I, O>> nodes,
															   final ADSNode<S, I, O> current) {
		if (current.isLeaf()) {
			nodes.add(current);
		}
		else {
			for (ADSNode<S, I, O> n : current.getChildren().values()) {
				collectFinalNodesRecursively(nodes, n);
			}
		}
	}

	public static <S, I, O> Pair<Word<I>, Word<O>> buildTraceForFinalNode(final ADSNode<S, I, O> node) {

		ADSNode<S, I, O> parentIter = node.getParent();
		ADSNode<S, I, O> nodeIter = node;

		final WordBuilder<I> inputBuilder = new WordBuilder<>();
		final WordBuilder<O> outputBuilder = new WordBuilder<>();

		while (parentIter != null) {
			inputBuilder.append(parentIter.getSymbol());
			outputBuilder.append(getOutputForSuccessor(parentIter, nodeIter));

			nodeIter = parentIter;
			parentIter = parentIter.getParent();
		}

		return new Pair<>(inputBuilder.reverse().toWord(), outputBuilder.reverse().toWord());
	}

	public static <S, I, O> O getOutputForSuccessor(final ADSNode<S, I, O> node, final ADSNode<S, I, O> successor) {

		if (!successor.getParent().equals(node)) {
			throw new IllegalArgumentException("No parent relationship");
		}

		for (Map.Entry<O, ADSNode<S, I, O>> entry : node.getChildren().entrySet()) {
			if (entry.getValue().equals(successor)) {
				return entry.getKey();
			}
		}

		throw new IllegalArgumentException("No child relationship");
	}

}