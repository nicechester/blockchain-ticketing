// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

error CustomError();

contract TicketNFT is ERC721, Ownable {
    enum TicketStatus { Valid, Used, Canceled }
    mapping(uint256 => TicketStatus) public ticketStatus;

    struct TicketInfo {
        uint256 visitDate; // Unix timestamp
        string park;
    }
    mapping(uint256 => TicketInfo) public ticketInfo;

    uint256 public nextTokenId;

    // Add the event for minting
    event TicketMinted(address indexed to, uint256 indexed tokenId, uint256 visitDate, string park);

    constructor(address initialOwner) ERC721("ThemeParkTicket", "TPTKT") Ownable(initialOwner) {}

    function mint(address to, uint256 visitDate, string memory park) public onlyOwner {
        uint256 tokenId = nextTokenId;
        _safeMint(to, nextTokenId);
        ticketStatus[nextTokenId] = TicketStatus.Valid;
        ticketInfo[nextTokenId] = TicketInfo(visitDate, park);
        nextTokenId++;
        emit TicketMinted(to, tokenId, visitDate, park);
    }

    function useTicket(uint256 tokenId) public {
        require(ticketStatus[tokenId] == TicketStatus.Valid, "Ticket not valid");
        ticketStatus[tokenId] = TicketStatus.Used;
    }

    function cancelTicket(uint256 tokenId) public onlyOwner {
        require(ticketStatus[tokenId] == TicketStatus.Valid, "Ticket not valid");
        ticketStatus[tokenId] = TicketStatus.Canceled;
    }

    function getTicketStatus(uint256 tokenId) public view returns (TicketStatus) {
        return ticketStatus[tokenId];
    }

    function getTicketInfo(uint256 tokenId) public view returns (uint256, string memory) {
        TicketInfo memory info = ticketInfo[tokenId];
        return (info.visitDate, info.park);
    }

    function getTicketsOf(address account) public view returns (uint256[] memory) {
        uint256 balance = balanceOf(account);
        uint256[] memory tokenIds = new uint256[](balance);
        uint256 count = 0;
        uint256 supply = nextTokenId;
        for (uint256 i = 0; i < supply; i++) {
            try this.ownerOf(i) returns (address owner) {
                if (owner == account) {
                    tokenIds[count] = i;
                    count++;
                }
            } catch {
                // Token does not exist, skip
            }
        }
        return tokenIds;
    }
}