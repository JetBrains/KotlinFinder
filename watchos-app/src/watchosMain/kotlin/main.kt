import kotlinx.cinterop.*
import platform.Foundation.*
import platform.WatchKit.*
import platform.darwin.NSObject

// Standard entry point for the WatchKit applications.
@SymbolName("WKExtensionMain")
external fun WKExtensionMain(argc: Int, argv: CValues<CPointerVar<ByteVar>>)

fun main(args: Array<String>) {
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("konan") + args).map { it.cstr.ptr }.toCValues()

        autoreleasepool {
            println("main entered")
            WKExtensionMain(argc, argv)
        }
    }
}