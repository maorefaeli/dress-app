/**
 * Utility class for finding cycles within directed graph
 */
class Graph {
    constructor() {
        this.nodes = [];
        this.arrows = new Map();
        this.data = new Map();
    }

    /**
     * Add new vertex
     * @param {string} vertex vertex identifier
     */
    addVertex(vertex) {
        this.nodes.push(vertex);
        this.arrows.set(vertex, []);
    }

    /**
     * Add new edge
     * @param {string} from source vertex identifier
     * @param {string} to target vertex identifier
     * @param {any=} data optional data to store on the edge
     */
    addEdge(from, to, data) {
        const values = this.arrows.get(from);
        values.push(to);
        this.arrows.set(from, values);
        this.data.set([from, to].join(), data);
    }

    /**
     * Find all the elementary cycles of a directed graph.
     * Returns an array of cycles.
     * Each cycle is an array of 2 element tuples (represented by array) in the order of the cycle.
     * Each tuple is [vertex, data]
     */
    findCycles() {
        let startNode;
        const stack = [];
        const cycles = [];
        const blocked = new Map();

        // book keeping to prevent algorithm fruitless searches
        const b = new Map();
        const graph = this;

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

            // Cycle is valid
            cycles.push(cycle);
        }

        // Unblock a node recursively from all book keeping
        function unblock(node) {
            blocked.set(node, false);
            if (b.has(node)) {
                b.get(node).forEach(function (w) {
                    b.get(node).delete(w);
                    if (blocked.get(w)) {
                        unblock(w);
                    }
                });
            }
        }

        // Use DFS to locate all the cycles starting from a specific node
        function findCyclesFromNode(node) {
            let found = false;
            stack.push(node);
            blocked.set(node, true);
            graph.arrows.get(node).forEach(function (w) {
                // If reached startNode, cycle was detected
                if (w === startNode) {
                    found = true;
                    addCycle(startNode, stack);

                // As long as not blocked keep going
                } else if (!blocked.get(w)) {
                    if (findCyclesFromNode(w)) {
                        found = true;
                    }
                }
            });

            // Hit a cycle
            if (found) {
                unblock(node);
            } else {
                // If cycle not found, store the data on book keeping to not encounter it again
                graph.arrows.get(node).forEach(function (w) {
                    let entry = b.get(w);
                    if (!entry) {
                        entry = new Set();
                        b.set(w, entry);
                    }
                    entry.add(node);
                });
            }

            // Done with the node
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
    
                    // Get the next one that connected to current, cyclic iteration
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

        // Add the data stored on the edges between nodes
        return fillCyclesWithData(cycles);
    }
}

module.exports = Graph;
