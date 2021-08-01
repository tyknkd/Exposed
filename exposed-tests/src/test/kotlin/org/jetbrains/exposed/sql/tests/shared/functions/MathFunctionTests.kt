package org.jetbrains.exposed.sql.tests.shared.functions

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.functions.math.*
import org.jetbrains.exposed.sql.tests.TestDB
import org.jetbrains.exposed.sql.tests.shared.expectException
import org.junit.Test
import java.math.BigDecimal

class MathFunctionTests : FunctionsTestBase() {

    @Test
    fun testAbsFunction() {
        withTable {
            assertExpressionEqual(0, AbsFunction(intLiteral(0)))
            assertExpressionEqual(100, AbsFunction(intLiteral(100)))
            assertExpressionEqual(100, AbsFunction(intLiteral(-100)))
            assertExpressionEqual(100.0, AbsFunction(doubleLiteral(100.0)))
            assertExpressionEqual(100.0, AbsFunction(doubleLiteral(-100.0)))
        }
    }

    @Test
    fun testSignFunction() {
        withTable {
            assertExpressionEqual(0, SignFunction(intLiteral(0)))
            assertExpressionEqual(1, SignFunction(intLiteral(100)))
            assertExpressionEqual(-1, SignFunction(intLiteral(-100)))
            assertExpressionEqual(1, SignFunction(doubleLiteral(100.0)))
            assertExpressionEqual(-1, SignFunction(doubleLiteral(-100.0)))
        }
    }

    @Test
    fun testFloorFunction() {
        withTable {
            assertExpressionEqual(100, FloorFunction(intLiteral(100)))
            assertExpressionEqual(-100, FloorFunction(intLiteral(-100)))
            assertExpressionEqual(100, FloorFunction(doubleLiteral(100.0)))
            assertExpressionEqual(100, FloorFunction(doubleLiteral(100.30)))
            assertExpressionEqual(100, FloorFunction(doubleLiteral(100.70)))
            assertExpressionEqual(-100, FloorFunction(doubleLiteral(-100.0)))
            assertExpressionEqual(-101, FloorFunction(doubleLiteral(-100.30)))
            assertExpressionEqual(-101, FloorFunction(doubleLiteral(-100.70)))
        }
    }

    @Test
    fun testCeilFunction() {
        withTable {
            assertExpressionEqual(100, CeilFunction(intLiteral(100)))
            assertExpressionEqual(-100, CeilFunction(intLiteral(-100)))
            assertExpressionEqual(100, CeilFunction(doubleLiteral(100.0)))
            assertExpressionEqual(101, CeilFunction(doubleLiteral(100.30)))
            assertExpressionEqual(101, CeilFunction(doubleLiteral(100.70)))
            assertExpressionEqual(-100, CeilFunction(doubleLiteral(-100.0)))
            assertExpressionEqual(-100, CeilFunction(doubleLiteral(-100.30)))
            assertExpressionEqual(-100, CeilFunction(doubleLiteral(-100.70)))
        }
    }

    @Test
    fun testPowerFunction() {
        withTable {
            assertExpressionEqual(BigDecimal(100), PowerFunction(intLiteral(10), intLiteral(2)))
            assertExpressionEqual(BigDecimal(100), PowerFunction(intLiteral(10), doubleLiteral(2.0)))
            assertExpressionEqual(BigDecimal("102.01"), PowerFunction(doubleLiteral(10.1), intLiteral(2)))
            assertExpressionEqual(BigDecimal("102.01"), PowerFunction(doubleLiteral(10.1), doubleLiteral(2.0)))
            assertExpressionEqual(BigDecimal("102.01"), PowerFunction(decimalLiteral(BigDecimal("10.1")), intLiteral(2)))
            assertExpressionEqual(BigDecimal("102.01"), PowerFunction(decimalLiteral(BigDecimal("10.1")), doubleLiteral(2.0)))
        }
    }

    @Test
    fun testRoundFunction() {
        withTable {
            assertExpressionEqual(BigDecimal(10), RoundFunction(intLiteral(10), 0))
            assertExpressionEqual(BigDecimal("10.00"), RoundFunction(intLiteral(10), 2))
            assertExpressionEqual(BigDecimal(10), RoundFunction(doubleLiteral(10.455), 0))
            assertExpressionEqual(BigDecimal(11), RoundFunction(doubleLiteral(10.555), 0))
            assertExpressionEqual(BigDecimal("10.56"), RoundFunction(doubleLiteral(10.555), 2))
        }
    }

    @Test
    fun testSqrtFunction() {
        withTable { testDb ->
            assertExpressionEqual(BigDecimal(10), SqrtFunction(intLiteral(100)))
            assertExpressionEqual(BigDecimal(10), SqrtFunction(doubleLiteral(100.0)))
            assertExpressionEqual(BigDecimal("11.2"), SqrtFunction(doubleLiteral(125.44)))
            assertExpressionEqual(BigDecimal(10), SqrtFunction(decimalLiteral(BigDecimal(100))))
            assertExpressionEqual(BigDecimal("11.2"), SqrtFunction(decimalLiteral(BigDecimal("125.44"))))

            when (testDb) {
                TestDB.MYSQL, TestDB.MARIADB -> {
                    assertExpressionEqual(null, SqrtFunction(intLiteral(-100)))
                }
                TestDB.SQLITE, TestDB.POSTGRESQL, TestDB.POSTGRESQLNG -> {
                    // SQLite, PSQL fails to execute sqrt with negative value
                    expectException<ExposedSQLException> {
                        assertExpressionEqual(null, SqrtFunction(intLiteral(-100)))
                    }
                }
                else -> {
                    expectException<IllegalStateException> {
                        assertExpressionEqual(null, SqrtFunction(intLiteral(-100)))
                    }
                }
            }
        }
    }

    @Test
    fun testExpFunction() {
        withTable {
            assertExpressionEqual(BigDecimal("2.718281828459045"), ExpFunction(intLiteral(1)))
            assertExpressionEqual(BigDecimal("12.182493960703473"), ExpFunction(doubleLiteral(2.5)))
            assertExpressionEqual(BigDecimal("12.182493960703473"), ExpFunction(decimalLiteral(BigDecimal("2.5"))))
        }
    }
}