package net.automatalib.util.automata.ads;

import net.automatalib.automata.ads.ADSNode;
import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Word;
import org.testng.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author frohme
 */
public class AbstractADSTest {

	protected <I, O> void verifySuccess(final CompactMealy<I, O> mealy) {
		this.verifySuccess(mealy, mealy.getStates());
	}

	protected <I, O> void verifySuccess(final CompactMealy<I, O> mealy, final Collection<Integer> targets) {
		this.verifySuccess(mealy, new HashSet<>(targets));
	}

	protected <I, O> void verifySuccess(final CompactMealy<I, O> mealy, final Set<Integer> targets) {
		final Optional<ADSNode<Integer, I, O>> defaultADS = ADS.compute(mealy, mealy.getInputAlphabet(), targets);
		final Optional<ADSNode<Integer, I, O>> bestEffortADS =
				BacktrackingSearch.compute(mealy, mealy.getInputAlphabet(), targets);
		final Optional<ADSNode<Integer, I, O>> bfsMinLengthADS = BacktrackingSearch.computeOptimal(mealy,
																								   mealy.getInputAlphabet(),
																								   targets,
																								   BacktrackingSearch.CostAggregator.MIN_LENGTH);
		final Optional<ADSNode<Integer, I, O>> bfsMinSizeADS = BacktrackingSearch.computeOptimal(mealy,
																								 mealy.getInputAlphabet(),
																								 targets,
																								 BacktrackingSearch.CostAggregator.MIN_SIZE);

		this.verifySuccess(mealy, targets, defaultADS);
		this.verifySuccess(mealy, targets, bestEffortADS);
		this.verifySuccess(mealy, targets, bfsMinLengthADS);
		this.verifySuccess(mealy, targets, bfsMinSizeADS);

		final int defaultLength = ADSUtil.computeLength(defaultADS.get());
		final int bestEffortLength = ADSUtil.computeLength(bestEffortADS.get());
		final int bfsMinLengthLength = ADSUtil.computeLength(bfsMinLengthADS.get());
		final int bfsMinSizeLength = ADSUtil.computeLength(bfsMinSizeADS.get());

		Assert.assertTrue(bfsMinLengthLength <= defaultLength);
		Assert.assertTrue(bfsMinLengthLength <= bestEffortLength);
		Assert.assertTrue(bfsMinLengthLength <= bfsMinSizeLength);

		final int defaultSize = ADSUtil.countSymbolNodes(defaultADS.get());
		final int bestEffortSize = ADSUtil.countSymbolNodes(bestEffortADS.get());
		final int bfsMinLengthSize = ADSUtil.countSymbolNodes(bfsMinLengthADS.get());
		final int bfsMinSizeSize = ADSUtil.countSymbolNodes(bfsMinSizeADS.get());

		Assert.assertTrue(bfsMinSizeSize <= defaultSize);
		Assert.assertTrue(bfsMinSizeSize <= bestEffortSize);
		Assert.assertTrue(bfsMinSizeSize <= bfsMinLengthSize);
	}

	protected <I, O> void verifySuccess(final CompactMealy<I, O> mealy,
										final Set<Integer> targets,
										final Optional<ADSNode<Integer, I, O>> potentialADS) {

		Assert.assertNotNull(potentialADS);
		Assert.assertTrue(potentialADS.isPresent());

		final ADSNode<Integer, I, O> ads = potentialADS.get();
		final Set<ADSNode<Integer, I, O>> finalNodes = ADSUtil.collectFinalNodes(ads);

		Assert.assertEquals(targets, finalNodes.stream().map(ADSNode::getHypothesisState).collect(Collectors.toSet()));

		final Map<ADSNode<Integer, I, O>, Pair<Word<I>, Word<O>>> traces =
				finalNodes.stream().collect(Collectors.toMap(Function.identity(), ADSUtil::buildTraceForFinalNode));

		// check matching outputs
		for (Map.Entry<ADSNode<Integer, I, O>, Pair<Word<I>, Word<O>>> entry : traces.entrySet()) {
			final Integer state = entry.getKey().getHypothesisState();
			final Word<I> input = entry.getValue().getFirst();
			final Word<O> output = entry.getValue().getSecond();
			Assert.assertEquals(mealy.computeStateOutput(state, input), output);
		}

		// check uniqueness of ADSs
		final Set<Word<O>> outputSet = traces.values().stream().map(Pair::getSecond).collect(Collectors.toSet());
		Assert.assertEquals(traces.size(), outputSet.size());
	}

	protected <I, O> void verifyFailure(final CompactMealy<I, O> mealy) {
		this.verifyFailure(mealy, mealy.getStates());
	}

	protected <I, O> void verifyFailure(final CompactMealy<I, O> mealy, final Collection<Integer> targets) {
		this.verifyFailure(mealy, new HashSet<>(targets));
	}

	protected <I, O> void verifyFailure(final CompactMealy<I, O> mealy, final Set<Integer> targets) {
		final Optional<ADSNode<Integer, I, O>> defaultADS = ADS.compute(mealy, mealy.getInputAlphabet(), targets);
		final Optional<ADSNode<Integer, I, O>> bestEffortADS =
				BacktrackingSearch.compute(mealy, mealy.getInputAlphabet(), targets);
		final Optional<ADSNode<Integer, I, O>> bfsMinLengthADS = BacktrackingSearch.computeOptimal(mealy,
																								   mealy.getInputAlphabet(),
																								   targets,
																								   BacktrackingSearch.CostAggregator.MIN_LENGTH);
		final Optional<ADSNode<Integer, I, O>> bfsMinSizeADS = BacktrackingSearch.computeOptimal(mealy,
																								 mealy.getInputAlphabet(),
																								 targets,
																								 BacktrackingSearch.CostAggregator.MIN_SIZE);

		Assert.assertFalse(defaultADS.isPresent());
		Assert.assertFalse(bestEffortADS.isPresent());
		Assert.assertFalse(bfsMinLengthADS.isPresent());
		Assert.assertFalse(bfsMinSizeADS.isPresent());
	}
}