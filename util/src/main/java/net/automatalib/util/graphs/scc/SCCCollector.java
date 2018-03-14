/* Copyright (C) 2013-2018 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.automatalib.util.graphs.scc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SCCCollector<N> implements SCCListener<N> {

    private final Map<Integer, List<N>> sccs = new HashMap<>();

    @Override
    public void foundSCCNode(Integer lowLink, N scc) {
        List<N> sccForLowLink = sccs.getOrDefault(lowLink, new ArrayList<>());
        sccForLowLink.add(scc);
        sccs.put(lowLink, sccForLowLink);
    }

    @Nonnull
    public List<List<N>> getSCCList() {
        return new ArrayList<>(sccs.values());
    }

}
