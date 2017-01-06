package net.automatalib.util.automata.ads;

import net.automatalib.automata.ads.ADSNode;
import net.automatalib.automata.ads.impl.ADSFinalNode;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Alphabet;

import java.util.Optional;
import java.util.Set;

/**
 * General purpose facade for computing adaptive distinguishing sequences. Delegates to {@link LeeYannakakis},
 * {@link BacktrackingSearch} (non-optimal) and {@link StateEquivalence} for the actual computation of the ADS.
 *
 * @author frohme
 */
public class ADS {

	/**
	 * Compute an adaptive distinguishing sequence for the given automaton and the given set of states.
	 *
	 * @param automaton the automaton for which an ADS should be computed
	 * @param input     the input alphabet of the automaton
	 * @param states    the set of states for should be distinguished by the computed ADS
	 * @param <S>       (hypothesis) state type
	 * @param <I>       input alphabet type
	 * @param <O>       output alphabet type
	 * @return {@code Optional.empty()} if there exists no ADS that distinguishes the given states, a valid ADS
	 * otherwise.
	 */
	public static <S, I, O> Optional<ADSNode<S, I, O>> compute(final MealyMachine<S, I, ?, O> automaton,
															   final Alphabet<I> input,
															   final Set<S> states) {

		if (states.isEmpty()) {
			return Optional.empty();
		}
		else if (states.size() == 1) {
			final S singleState = states.iterator().next();
			return Optional.of(new ADSFinalNode<>(null, singleState));
		}
		else if (states.size() == 2) {
			return StateEquivalence.compute(automaton, input, states);
		}
		else if (states.size() == automaton.getStates().size()) {
			final LYResult<S, I, O> result = LeeYannakakis.compute(automaton, input);
			if (result.isPresent()) {
				return Optional.of(result.get());
			}
			return Optional.empty();
		}
		else {
			return BacktrackingSearch.compute(automaton, input, states);
		}
	}

	/**
	 * See {@link #compute(MealyMachine, Alphabet, Set)}. Internal version that uses a {@link SplitTree} for state
	 * tracking.
	 */
	static <S, I, O> Optional<ADSNode<S, I, O>> compute(final MealyMachine<S, I, ?, O> automaton,
														final Alphabet<I> input,
														final SplitTree<S, I, O> node) throws IllegalArgumentException {

		if (node.getPartition().isEmpty()) {
			throw new IllegalArgumentException("Empty partitions should be handled by the specific algorithm");
		}
		else if (node.getPartition().size() == 1) {

			final S state = node.getPartition().iterator().next();

			if (!node.getMapping().containsKey(state)) {
				throw new IllegalStateException();
			}

			final ADSNode<S, I, O> result = new ADSFinalNode<>(null, node.getMapping().get(state));
			return Optional.of(result);
		}
		else if (node.getPartition().size() == 2) {
			return StateEquivalence.compute(automaton, input, node);
		}
		else if (node.getPartition().size() == automaton.getStates().size()) {
			throw new IllegalArgumentException("Call LeeYannakakis directly");
		}
		else {
			return BacktrackingSearch.compute(automaton, input, node);
		}
	}
}