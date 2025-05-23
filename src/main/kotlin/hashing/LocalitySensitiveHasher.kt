package software.bevel.graph_domain.hashing

/**
 * Defines a contract for locality-sensitive hashing (LSH).
 * LSH algorithms aim to hash similar input items to the same "buckets" with high probability,
 * allowing for efficient similarity searches.
 */
interface LocalitySensitiveHasher {
    /**
     * Computes a locality-sensitive hash for the given input string.
     *
     * @param input The string to hash.
     * @return A string representation of the hash.
     */
    fun hash(input: String): String

    /**
     * Returns a value between 0 and 1 representing the similarity percentage.
     */
    fun similarity(hash1: String, hash2: String): Double
}