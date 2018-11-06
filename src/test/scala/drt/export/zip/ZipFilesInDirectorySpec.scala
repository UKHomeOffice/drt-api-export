package drt.export.zip

import java.io.File
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import uk.gov.homeoffice.drt.export.zip.ZipFilesInDirectory

class ZipFilesInDirectorySpec extends Specification {

  trait Context extends Scope with ZipFilesInDirectory {
    override val directory: String = new File("src/test/resources/zippedtest").getCanonicalPath
  }

  "ZipFilesInDirectory" should {

    "list files in directory in order of earliest date in filename first" in new Context {
      val result: List[String] = getFilesInDirectory()

      result mustEqual List("drt_dq_150618_165737_5153.zip", "drt_dq_160617_165737_5153.zip", "drt_dq_160717_165737_5153.zip", "drt_dq_170617_165737_5153.zip")
    }

    "list files in directory in order of earliest date in filename first from a starting filename" in new Context {
      val result: List[String] = getFilesInDirectory(Some("drt_dq_160717_165737_5153.zip"))

      result mustEqual List("drt_dq_160717_165737_5153.zip", "drt_dq_170617_165737_5153.zip")
    }

    "Can unzip all zipped DQ files" in new Context {
      val result = unzipFile("drt_dq_160617_165737_5153.zip")

      result.size mustEqual 59
    }

  }

}
