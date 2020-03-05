package com.ice.ktuice.al.services.scrapers.base.ktuScraperService.util

import com.ice.ktuice.al.services.scrapers.base.exceptions.ParsingException

/**
 * Grade table is opened by an url that requires data from
 * a js function called `infivert`
 * for example: `infivert(624236,'2019P6140881','IicVnqt30l'+'0-t7fCRw1YUw')`
 * or: `infivert(4738669,'2019P6141401','IicVnqt30l1SXOTrHHdj6g')`
 * these arguments must be parsed and passed to the endpoint that serves grades
 */
class JsFunctionParser(
        /**
         * the whole on-click call of infivert, such as:
         * infivert(624236,'2019P6140881','IicVnqt30l'+'0-t7fCRw1YUw')
         */
        private val sourceFunction: String
){
    private val args: List<String>
    init {
        args = parseArguments()
    }
    fun getName(): String {
        return sourceFunction.split('(')[0]
    }

    fun getArgument(id: Int): String {
        return args[id]
    }

    private fun parseArguments(): List<String> {
        var argPart = sourceFunction.split('(')[1]
//         remove all whitespace
//        argPart = argPart.replace("\\s".toRegex(), "")
        // remove ending bracket
        if(argPart.endsWith(')')){
            argPart = argPart.removeSuffix(")")
        }

        val arguments = argPart.split(",")
        return arguments.map {
            val simplified = simplifyAST(parseToAST(it))?.value
                    ?: throw ParsingException("Failed parsing js function argument: $it")
            simplified
        }.toList()
    }

    enum class SimpleASTNodeType {
        OperatorAdd, LiteralString, LiteralNumber
    }

    class SimpleASTNode(
            var type: SimpleASTNodeType,
            var value: String
    ){
        override fun toString(): String {
            return value
        }
        companion object{
            fun parseOperator(s: String): SimpleASTNode{
                if(s == "+"){
                    return SimpleASTNode(
                            SimpleASTNodeType.OperatorAdd,
                            s
                    )
                }
                throw ParsingException("Can not parse operator AST node: $s")
            }

            fun parseLiteral(s: String): SimpleASTNode {
                val literalType = if(s.startsWith('\'') || s.startsWith('\"')){
                    SimpleASTNodeType.LiteralString
                }else{
                    SimpleASTNodeType.LiteralNumber
                }
                val value = removeChars(s, "\'\"")!!
                return SimpleASTNode(
                        literalType,
                        value
                )
            }

            private fun removeChars(target: String?, chars: String): String? {
                var result = target
                for(c in chars){
                    result = result?.replace(c.toString(), "")
                }
                return result
            }
        }
    }

    /**
     * Simple state-machine based expression parser
     * assumes that everything is either a binary expression
     * or a literal
     */
    private fun parseToAST(s: String): MutableList<SimpleASTNode> {
        val literalEnds = mutableListOf<Int>()
        var inLiteral = false
        var i = 0
        for(c in s){
            if(c.isDigit() || c.isLetter()){
                if(!inLiteral){
                    literalEnds.add(i)
                }
                inLiteral = true
            }else if(c == '\'' || c == '\"'){
                if(inLiteral) {
                    literalEnds.add(i+1)
                }else{
                    literalEnds.add(i)
                }
                inLiteral = !inLiteral
            }
            i++
        }
        literalEnds.add(i)

        val astNodeList = mutableListOf<SimpleASTNode>()
        for(j in (0 until literalEnds.size/2)){
            val literal = s.substring(literalEnds[j*2], literalEnds[j*2 + 1])
            if((j*2-1) >= 0){
                val operator = s.substring(literalEnds[j*2-1], literalEnds[j*2])
                astNodeList.add(SimpleASTNode.parseOperator(operator))
            }
            astNodeList.add(SimpleASTNode.parseLiteral(literal))
        }
        return astNodeList
    }

    private fun simplifyAST(astList: MutableList<SimpleASTNode>): SimpleASTNode? {
        while(astList.size > 1){
            for(i in (0 until astList.size)){
                if(astList[i].type == SimpleASTNodeType.OperatorAdd){
                    val lhs = astList[i-1]
                    val rhs = astList[i+1]
                    val resultNode = if(lhs.type == SimpleASTNodeType.LiteralString || rhs.type == SimpleASTNodeType.LiteralString){
                        SimpleASTNode(
                                SimpleASTNodeType.LiteralString,
                                "${lhs.value}${rhs.value}"
                        )
                    }else{
                        // i hope this will never be used (currently the literal parser does not support it)
                        SimpleASTNode(
                                SimpleASTNodeType.LiteralNumber,
                                "${lhs.value.toInt() + rhs.value.toInt()}"
                        )
                    }
                    astList.removeAt(i-1)
                    astList.removeAt(i-1)
                    astList[i-1] = resultNode
                    break // break out of ast iteration
                }
            }
        }
        return astList.firstOrNull()
    }

}