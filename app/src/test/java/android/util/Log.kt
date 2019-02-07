package andrkkoid.util

class LogBorth {
    companion object {
        fun d(tag: String, msg: String) = println("DEBUG $tag : $msg").let { 0 }
        fun i(tag: String, msg: String) = println("INFO $tag : $msg").let { 0 }
        fun w(tag: String, msg: String) = println("WARN $tag : $msg").let { 0 }
        fun e(tag: String, msg: String) = println("ERROR $tag : $msg").let { 0 }
        fun e(tag: String, msg: String, t: Throwable) = println("ERROR $tag : $msg").let { 0 }
    }
}
