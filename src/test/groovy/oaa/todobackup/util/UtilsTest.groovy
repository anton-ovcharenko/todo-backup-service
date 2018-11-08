package oaa.todobackup.util

import spock.lang.Specification
import spock.lang.Unroll

import static oaa.todobackup.util.Utils.splitListBySize

class UtilsTest extends Specification {

    @Unroll
    def "splitListBySize should #description"() {
        expect:
        output == splitListBySize(input, size)

        where:
        description                 | size | input           | output
        "process empty list"        | 1    | []              | []
        "split big list on smaller" | 3    | [1, 2, 3, 4, 5] | [[1, 2, 3], [4, 5]]
        "not split small list"      | 3    | [1, 2]          | [[1, 2]]
    }

    @Unroll
    def "splitListBySize should throw #exception when #description"() {
        when:
        splitListBySize(input, size)
        then:
        thrown(exception)

        where:
        exception                | description        | size | input
        NullPointerException     | "list is null"     | 1    | null
        IllegalArgumentException | "size is 0"        | 0    | [1, 2, 3, 4, 5]
        IllegalArgumentException | "size is negative" | -1   | [1, 2]
    }
}
