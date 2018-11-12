package drt.export.zip

import java.io.File
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import uk.gov.homeoffice.drt.export.zip.ZipFilesInDirectory

class ZipFilesInDirectorySpec extends Specification {

  val directory: String = new File("src/test/resources/zippedtest").getCanonicalPath

  "ZipFilesInDirectory" should {

    val zipFilesInDirectory = ZipFilesInDirectory(directory)

    "list files in directory in order of earliest date in filename first" in {
      val result: List[String] = zipFilesInDirectory.filesInDirectory()

      result mustEqual List("drt_dq_150618_165737_5153.zip", "drt_dq_160617_165737_5153.zip", "drt_dq_160717_165737_5153.zip", "drt_dq_170617_165737_5153.zip")
    }

    "list files in directory in order of earliest date in filename first from a starting filename" in {
      val result: List[String] = zipFilesInDirectory.filesInDirectory(Some("drt_dq_160717_165737_5153.zip"))

      result mustEqual List("drt_dq_160717_165737_5153.zip", "drt_dq_170617_165737_5153.zip")
    }

    "Can unzip all zipped DQ files" in {
      val result = zipFilesInDirectory.unzipFile("drt_dq_160617_165737_5153.zip")

      result.size mustEqual 59
    }

  }

}
