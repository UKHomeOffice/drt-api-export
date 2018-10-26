package drt.export

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import uk.gov.homeoffice.drt.export.ZipFilesInDirectory

class ZipFilesInDirectorySpec extends Specification {

  trait Context extends Scope with ZipFilesInDirectory {
    override val directory: String = "zippedtest"
  }

  "ZipFilesInDirectory" should {

    "list some file" in new Context {

      val result = getSmallestFileInDirectory

      result mustEqual "drt_dq_160617_165737_5153.zip"
    }

  }



}
