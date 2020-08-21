class Graph {
    constructor() {
        this.nodes = [];
        this.arrows = new Map();
        this.data = new Map();
    }

    addVertex(vertex) {
        this.nodes.push(vertex);
        this.arrows.set(vertex, []);
    }

    addEdge(from, to, data) {
        const values = this.arrows.get(from);
        values.push(to);
        this.arrows.set(from, values);
        this.data.set([from, to].join(), data);
    }

    // Find all the elementary circuits of a directed graph
    findCycles() {
        var startNode;
        var stack = [];
        var cycles = [];
        var blocked = new Map();

        // book keeping to prevent Tarjan's algorithm fruitless searches
        var b = new Map();
        var graph = this;

        function addCycle(start, stack) {
            const cycle = [].concat(stack).concat(start);
            // Make sure all nodes unique
            if (new Set(cycle).size !== cycle.length) {
                return;
            }

            // Prevent duplicates
            const cycleAsString = [...cycle].sort().join();            
            if (cycles.some(c => [...c].sort().join() === cycleAsString)) {
                return;
            }

            cycles.push(cycle);
        }

        function unblock(u) {
            blocked.set(u, false);
            if (b.has(u)) {
                b.get(u).forEach(function (w) {
                    b.get(u).delete(w);
                    if (blocked.get(w)) {
                        unblock(w);
                    }
                });
            }
        }

        // Use DFS to locate all the cycles starting from a specific node
        function findCyclesFromNode(node) {
            var found = false;
            stack.push(node);
            blocked.set(node, true);
            graph.arrows.get(node).forEach(function (w) {
                if (w === startNode) {
                    found = true;
                    addCycle(startNode, stack);
                } else if (!blocked.get(w)) {
                    if (findCyclesFromNode(w)) {
                        found = true;
                    }
                }
            });

            if (found) {
                unblock(node);
            } else {
                graph.arrows.get(node).forEach(function (w) {
                    var entry = b.get(w);
                    if (!entry) {
                        entry = new Set();
                        b.set(w, entry);
                    }
                    entry.add(node);
                });
            }
            stack.pop();
            return found;
        }

        // Take the raw cycle nodes and fill them with the data provided on edges
        function fillCyclesWithData() {
            const filledCycles = [];

            cycles.forEach(cycle => {
                const filledCycle = [];

                for (let i = 0; i < cycle.length; i++) {

                    // Get the current vertex
                    const current = cycle[i];
    
                    // Get the next one that connected to current
                    const next = cycle[(i + 1) % cycle.length];
                    
                    filledCycle[i] = [current, graph.data.get([current, next].join())];
                }

                filledCycles.push(filledCycle);
            });

            return filledCycles;
        }

        // Find cycles from every node
        graph.nodes.forEach(function (node) {
            startNode = node;
            graph.arrows.get(node).forEach(findCyclesFromNode);
        });

        return fillCyclesWithData(cycles);
    }
}

module.exports = Graph;
